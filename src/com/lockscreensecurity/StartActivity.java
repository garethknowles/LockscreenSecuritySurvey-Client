package com.lockscreensecurity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends FragmentActivity implements OnClickListener {

	public static class StartDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage(R.string.dialog_message).setTitle(
					R.string.dialog_title);

			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((StartActivity) getActivity()).startNewQuestion();
						}
					});
			builder.setNegativeButton(R.string.more_info,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((StartActivity) getActivity()).moreInfo();
						}
					});

			// Create the AlertDialog
			return builder.create();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_start);

		Button b = (Button) findViewById(R.id.start_button);
		b.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.start_button) {
			// Intent intent = new Intent(this, DotPattern.class);
			// startActivity(intent);
			if (checkQuestions()) {
				// Check For Server Update
				getQuestionsFromServer();
			}
			// Startup First Question
			if (!checkQuestions()) {
				startSurvey();
			} else {
				Toast toast = Toast
						.makeText(
								this,
								"No questions saved and could not connect to server - please try again later",
								Toast.LENGTH_LONG);
				toast.show();
			}
		} else {
		}
	}

	public void startSurvey() {
		DialogFragment newFragment = new StartDialog();
		newFragment.show(getSupportFragmentManager(), "Info");
	}

	public void moreInfo() {
		Intent intent = new Intent(this, MoreInfo.class);
		startActivity(intent);
	}

	public void startNewQuestion() {
		// Get Saved Question JSON String
		SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,
				0);
		String json = settings.getString("questionSet", "");
		int currentQuestion = settings.getInt("currentQuestion", 0);
		if (currentQuestion >= settings.getInt("questionCount", 0))
			return;

		if (json.length() == 0) {
			Log.e("Error", "Error Getitng Json, no string in Prefs");
			return;
		}
		try {
			// Parse jSon
			JSONArray questionsJson = new JSONArray(json);
			JSONObject q = questionsJson.getJSONObject(currentQuestion);
			JSONObject question = q.getJSONObject("question");
			int type = question.getInt("question_type");

			Intent intent;

			this.finish();

			switch (type) {
			case 1:
				// multi_choice
				intent = new Intent(this, MultiChoiceQuestion.class);
				startActivity(intent);
				break;
			case 2:
				// string_entry
				intent = new Intent(this, StringEntryQuestion.class);
				startActivity(intent);
				break;
			case 3:
				// multi_choice_string
				intent = new Intent(this, MultiChoiceStringQuestion.class);
				startActivity(intent);
				break;
			case 4:
				// lock_pattern
				intent = new Intent(this, LockPatternQuestion.class);
				startActivity(intent);
				break;
			case 5:
				// score_range
				intent = new Intent(this, ScoreRangeQuestion.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Boolean checkQuestions() {

		// Get Saved Question JSON String
		SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,
				0);
		String json = settings.getString("questionSet", "");

		if (json == null || json.length() == 0) {
			// First Run, get from json on file
			json = "[{\"question\":{\"question_id\":\"7\",\"question_type\":\"3\",\"question_text\":\"What is your gender?\",\"question_order\":\"0\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"7\",\"answer_text\":\"Male\"},{\"answer_id\":\"8\",\"answer_text\":\"Female\"}]},{\"question\":{\"question_id\":\"9\",\"question_type\":\"1\",\"question_text\":\"What is your age?\",\"question_order\":\"1\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"11\",\"answer_text\":\"Under 16\"},{\"answer_id\":\"12\",\"answer_text\":\"16-24\"},{\"answer_id\":\"13\",\"answer_text\":\"25-39\"},{\"answer_id\":\"14\",\"answer_text\":\"40-64\"},{\"answer_id\":\"15\",\"answer_text\":\"Over 65\"}]},{\"question\":{\"question_id\":\"19\",\"question_type\":\"1\",\"question_text\":\"What is your Handedness for writing?\",\"question_order\":\"2\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"48\",\"answer_text\":\"Left-Handed\"},{\"answer_id\":\"49\",\"answer_text\":\"Right-Handed\"},{\"answer_id\":\"50\",\"answer_text\":\"Ambidexterity\"}]},{\"question\":{\"question_id\":\"10\",\"question_type\":\"3\",\"question_text\":\"How would you describe the writing of your native language?\",\"question_order\":\"3\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"16\",\"answer_text\":\"Left-To-Right Latin Alphabet (e.g. English, Spanish, German)\"},{\"answer_id\":\"17\",\"answer_text\":\"Top-To-Bottom Logographic (e.g. Chinese, Japanese, Korean)\"},{\"answer_id\":\"18\",\"answer_text\":\"Right-To-Left Abjad Script (e.g. Arabic, Hebrew, Farsi, Urdu)\"},{\"answer_id\":\"19\",\"answer_text\":\"Left-To-Right Cyrillic Script (e.g. Russian, Serbian, Ukrainian)\"},{\"answer_id\":\"20\",\"answer_text\":\"Left-To-Right Abugida Script (e.g. Hindi, Bengali, Thai)\"}]},{\"question\":{\"question_id\":\"11\",\"question_type\":\"1\",\"question_text\":\"How would you describe your education level?\",\"question_order\":\"4\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"22\",\"answer_text\":\"None or very little\"},{\"answer_id\":\"23\",\"answer_text\":\"Secondary School or Highschool Level (GCSE, O-Level)\"},{\"answer_id\":\"24\",\"answer_text\":\"Further Education (A-Level, Sixth Form, Twelfth Grade)\"},{\"answer_id\":\"25\",\"answer_text\":\"Degree Level (Bachelor's)\"},{\"answer_id\":\"26\",\"answer_text\":\"Postgraduate Education (Master's, Doctorate)\"}]},{\"question\":{\"question_id\":\"12\",\"question_type\":\"1\",\"question_text\":\"How would you describe your knowledge of computer security?\",\"question_order\":\"5\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"27\",\"answer_text\":\"Very little knowledge\"},{\"answer_id\":\"28\",\"answer_text\":\"Some fundamental knowledge\"},{\"answer_id\":\"29\",\"answer_text\":\"Fair good understanding\"},{\"answer_id\":\"30\",\"answer_text\":\"Highly experienced expert in area\"}]},{\"question\":{\"question_id\":\"14\",\"question_type\":\"3\",\"question_text\":\"What form of lockscreen security do you currently use?\",\"question_order\":\"6\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"34\",\"answer_text\":\"Don't own a mobile device\"},{\"answer_id\":\"35\",\"answer_text\":\"Own a device but don't use any lockscreen security\"},{\"answer_id\":\"36\",\"answer_text\":\"Use a PIN authentication on device\"},{\"answer_id\":\"37\",\"answer_text\":\"Use password on device\"},{\"answer_id\":\"38\",\"answer_text\":\"Use a pattern recognition on device\"}]},{\"question\":{\"question_id\":\"18\",\"question_type\":\"3\",\"question_text\":\"Why do you use your current lockscreen?\",\"question_order\":\"7\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"42\",\"answer_text\":\"Don't own device or use lockscreen security\"},{\"answer_id\":\"43\",\"answer_text\":\"To Protect Personal Data\"},{\"answer_id\":\"44\",\"answer_text\":\"Due to Sensitive Business or Organisation Data\"},{\"answer_id\":\"45\",\"answer_text\":\"To stop friends or family making calls, emails and texts on my phone\"},{\"answer_id\":\"46\",\"answer_text\":\"To avoid device theft issues\"},{\"answer_id\":\"47\",\"answer_text\":\"To stop people fiddling with my phone\"}]},{\"question\":{\"question_id\":\"15\",\"question_type\":\"4\",\"question_text\":\"Please draw a simple, easy to remember lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"8\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"16\",\"question_type\":\"4\",\"question_text\":\"Next please draw a more complicated lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"9\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"17\",\"question_type\":\"1\",\"question_text\":\"Would you prefer to use a simple or more complicated pattern?\",\"question_order\":\"10\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"39\",\"answer_text\":\"Use the simplest, easy-to-remember pattern\"},{\"answer_id\":\"40\",\"answer_text\":\"Use a pattern somewhere between simple and complicated\"},{\"answer_id\":\"41\",\"answer_text\":\"Use a complicated, harder-to-remember pattern\"}]}]";
			//json = "[{\"question\":{\"question_id\":\"7\",\"question_type\":\"3\",\"question_text\":\"What is your gender?\",\"question_order\":\"0\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"7\",\"answer_text\":\"Male\"},{\"answer_id\":\"8\",\"answer_text\":\"Female\"}]},{\"question\":{\"question_id\":\"9\",\"question_type\":\"1\",\"question_text\":\"What is your age?\",\"question_order\":\"1\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"11\",\"answer_text\":\"Under 16\"},{\"answer_id\":\"12\",\"answer_text\":\"16-24\"},{\"answer_id\":\"13\",\"answer_text\":\"25-39\"},{\"answer_id\":\"14\",\"answer_text\":\"40-64\"},{\"answer_id\":\"15\",\"answer_text\":\"Over 65\"}]},{\"question\":{\"question_id\":\"10\",\"question_type\":\"3\",\"question_text\":\"How would you describe the writing of your native language?\",\"question_order\":\"2\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"16\",\"answer_text\":\"Left-To-Right Latin Alphabet (e.g. English, Spanish, German)\"},{\"answer_id\":\"17\",\"answer_text\":\"Top-To-Bottom Logographic (e.g. Chinese, Japanese, Korean)\"},{\"answer_id\":\"18\",\"answer_text\":\"Right-To-Left Abjad Script (e.g. Arabic, Hebrew, Farsi, Urdu)\"},{\"answer_id\":\"19\",\"answer_text\":\"Left-To-Right Cyrillic Script (e.g. Russian, Serbian, Ukrainian)\"},{\"answer_id\":\"20\",\"answer_text\":\"Left-To-Right Abugida Script (e.g. Hindi, Bengali, Thai)\"}]},{\"question\":{\"question_id\":\"11\",\"question_type\":\"1\",\"question_text\":\"How would you describe your education level?\",\"question_order\":\"3\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"22\",\"answer_text\":\"None or very little\"},{\"answer_id\":\"23\",\"answer_text\":\"Secondary School or Highschool Level (GCSE, O-Level)\"},{\"answer_id\":\"24\",\"answer_text\":\"Further Education (A-Level, Sixth Form, Twelfth Grade)\"},{\"answer_id\":\"25\",\"answer_text\":\"Degree Level (Bachelor's)\"},{\"answer_id\":\"26\",\"answer_text\":\"Postgraduate Education (Master's, Doctorate)\"}]},{\"question\":{\"question_id\":\"12\",\"question_type\":\"1\",\"question_text\":\"How would you describe your knowledge of computer security?\",\"question_order\":\"4\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"27\",\"answer_text\":\"Very little knowledge\"},{\"answer_id\":\"28\",\"answer_text\":\"Some fundamental knowledge\"},{\"answer_id\":\"29\",\"answer_text\":\"Fair good understanding\"},{\"answer_id\":\"30\",\"answer_text\":\"Highly experienced expert in area\"}]},{\"question\":{\"question_id\":\"14\",\"question_type\":\"3\",\"question_text\":\"What form of lockscreen security do you currently use?\",\"question_order\":\"5\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"34\",\"answer_text\":\"Don't own a mobile device\"},{\"answer_id\":\"35\",\"answer_text\":\"Own a device but don't use any lockscreen security\"},{\"answer_id\":\"36\",\"answer_text\":\"Use a PIN authentication on device\"},{\"answer_id\":\"37\",\"answer_text\":\"Use password on device\"},{\"answer_id\":\"38\",\"answer_text\":\"Use a pattern recognition on device\"}]},{\"question\":{\"question_id\":\"18\",\"question_type\":\"3\",\"question_text\":\"Why do you use your current lockscreen?\",\"question_order\":\"6\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"42\",\"answer_text\":\"Don't own device or use lockscreen security\"},{\"answer_id\":\"43\",\"answer_text\":\"To Protect Personal Data\"},{\"answer_id\":\"44\",\"answer_text\":\"Due to Sensitive Business or Organisation Data\"},{\"answer_id\":\"45\",\"answer_text\":\"To stop friends or family making calls, emails and texts on my phone\"},{\"answer_id\":\"46\",\"answer_text\":\"To avoid device theft issues\"},{\"answer_id\":\"47\",\"answer_text\":\"To stop people fiddling with my phone\"}]},{\"question\":{\"question_id\":\"15\",\"question_type\":\"4\",\"question_text\":\"Please draw a simple, easy to remember lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"7\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"16\",\"question_type\":\"4\",\"question_text\":\"Next please draw a more complicated lockscreen security pattern you would use. The pattern must connect atleast 4 dots.\",\"question_order\":\"8\",\"question_notes\":\"\"},\"answers\":[]},{\"question\":{\"question_id\":\"17\",\"question_type\":\"1\",\"question_text\":\"Would you prefer to use a simple or more complicated pattern?\",\"question_order\":\"9\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"39\",\"answer_text\":\"Use the simplest, easy-to-remember pattern\"},{\"answer_id\":\"40\",\"answer_text\":\"Use a pattern somewhere between simple and complicated\"},{\"answer_id\":\"41\",\"answer_text\":\"Use a complicated, harder-to-remember pattern\"}]}]";
			//json = "[{\"question\":{\"question_id\":\"7\",\"question_type\":\"3\",\"question_text\":\"What is your gender?\",\"question_order\":\"0\",\"question_notes\":\"\"},\"answers\":[{\"answer_id\":\"7\",\"answer_text\":\"Male\"},{\"answer_id\":\"8\",\"answer_text\":\"Female\"}]}]";
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("questionSet", json);
			editor.commit();
		}
		try {
			// Parse jSon
			JSONArray questionsJson = new JSONArray(json);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("currentQuestion", 0);
			editor.putInt("questionCount", questionsJson.length());
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}

		return false;
	}
	public void getQuestionsFromServer() {

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		String address = Settings.SITE_ADDRESS + "getQuestions.php";
		HttpGet httpGet = new HttpGet(address);

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("JSON", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast toast = Toast
					.makeText(
							this,
							"Could not currently connect to the server, please check your Wifi or 3G settings",
							Toast.LENGTH_LONG);
			toast.show();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			Toast toast = Toast
					.makeText(
							this,
							"Could not currently connect to the server, please check your Wifi or 3G settings",
							Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("questionSet", builder.toString());
		editor.commit();
		Toast toast = Toast.makeText(this, "Downloaded New Survey Data",
				Toast.LENGTH_LONG);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.download_button:
			getQuestionsFromServer();
			break;
		default:
			break;
		}
		return true;
	}
}
