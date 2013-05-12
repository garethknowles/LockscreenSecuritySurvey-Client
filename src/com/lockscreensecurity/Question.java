package com.lockscreensecurity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Question extends Activity {
	int question_id = 0;
	int question_num = 0;
	ArrayList<AnswerValue> answersArray;

	JSONObject question;
	JSONArray answers;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_question);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			answersArray = (ArrayList<AnswerValue>) bundle.get("answerArray");

		SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,
				0);
		String json = settings.getString("questionSet", "");
		question_num = settings.getInt("currentQuestion", 0);

		try {
			JSONArray questionsJson = new JSONArray(json);
			JSONObject q = questionsJson.getJSONObject(question_num);
			question = q.getJSONObject("question");
			answers = q.getJSONArray("answers");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void startNewQuestion() {

		// Increase Question Number
		question_num++;
		SharedPreferences settings = getSharedPreferences(Settings.PREFS_NAME,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("currentQuestion", question_num);
		editor.commit();

		// Get Saved Question JSON String
		String json = settings.getString("questionSet", "");
		if (question_num >= settings.getInt("questionCount", 0)) {
			// Go To End Page Here
			this.finish();
			Intent intent = new Intent(this, SendActivity.class);
			intent.putExtra("answerArray",
					(ArrayList<AnswerValue>) answersArray);
			startActivity(intent);
			return;
		}

		if (json.length() == 0) {
			Log.e("Error", "Error Getitng Json, no string in Prefs");
			return;
		}
		try {
			// Parse jSon
			JSONArray questionsJson = new JSONArray(json);
			JSONObject q = questionsJson.getJSONObject(question_num);
			JSONObject question = q.getJSONObject("question");
			int type = question.getInt("question_type");

			Intent intent;
			this.finish();

			switch (type) {
			case 1:
				// multi_choice
				intent = new Intent(this, MultiChoiceQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
				break;
			case 2:
				// string_entry
				intent = new Intent(this, StringEntryQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
				break;
			case 3:
				// multi_choice_string
				intent = new Intent(this, MultiChoiceStringQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
				break;
			case 4:
				// lock_pattern
				intent = new Intent(this, LockPatternQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
				break;
			case 5:
				// score_range
				intent = new Intent(this, ScoreRangeQuestion.class);
				intent.putExtra("answerArray",
						(ArrayList<AnswerValue>) answersArray);
				startActivity(intent);
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_multi_choice_question, menu);
	// return true;
	// }

	@Override
	public void onBackPressed() {
		// do whatever you want the 'Back' button to do
		// as an example the 'Back' button is set to start a new Activity named
		// 'NewActivity'
		this.startActivity(new Intent(this, StartActivity.class));

		return;
	}
}
