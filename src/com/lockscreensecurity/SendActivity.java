package com.lockscreensecurity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SendActivity extends Activity implements OnClickListener {

	ArrayList<AnswerValue> answersArray;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_send);

		Button b = (Button) findViewById(R.id.send_button);
		b.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			answersArray = (ArrayList<AnswerValue>) bundle.get("answerArray");

	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.send_button) {
			// Try Send Data
			attemptSend();
		} else {
		}
	}

	public void doRequest() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// Make Json From Answers
					JSONObject json = new JSONObject();
					JSONArray answers = new JSONArray();

					try {

						for (int i = 0; i < answersArray.size(); i++) {
							AnswerValue answer = answersArray.get(i);

							JSONObject a_json = new JSONObject();
							a_json.put("question_id", answer.question_id);
							a_json.put("answer_id", answer.answer_id);
							a_json.put("answer_string", answer.answer_string);

							answers.put(a_json);
						}
						String model = android.os.Build.MODEL;
						JSONObject a_json = new JSONObject();
						a_json.put("question_id", "0");
						a_json.put("answer_id", "");
						a_json.put("answer_string", model);
						answers.put(a_json);

						json.put("answers", answers);

					} catch (JSONException e) {
						e.printStackTrace();
					}

					// Send Json Data
					HttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(
							client.getParams(), 10000);

					String address = Settings.SITE_ADDRESS + "postData.php";
					try {
						HttpPost post = new HttpPost(address);
						StringEntity se = new StringEntity(json.toString());
						se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
								"application/json"));
						post.setEntity(se);

						final HttpResponse response = client.execute(post);

						String res = "";
						if (response != null) {
							InputStream in;
							try {
								in = response.getEntity().getContent();
								res = convertStreamToString(in);

							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}


						final String out = res.toString();
						
						Handler h = new Handler(
								SendActivity.this.getMainLooper());
						h.post(new Runnable() {
							@Override
							public void run() {
								handleResponse(out);
							}
						});

					} catch (Exception e) {
						e.printStackTrace();
						Handler h = new Handler(
								SendActivity.this.getMainLooper());
						h.post(new Runnable() {
							@Override
							public void run() {
								showInternetError();
							}
						});
						return;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();
	}

	public void showInternetError() {
		Toast toast = Toast
				.makeText(
						this,
						"Could not currently connect to the server, please check your Wifi or 3G settings",
						Toast.LENGTH_LONG);
		toast.show();
	}

	public void handleResponse(String response) {
		/* Checking response */
		if (Integer.valueOf(response) == 1) {
			// Success Response
			Toast toast = Toast.makeText(this,
					"Server Successfully Recieved Data", Toast.LENGTH_LONG);
			toast.show();
			Intent intent = new Intent(this, SurveyEnd.class);
			startActivity(intent);
			this.finish();
		} else {
			// Wrong Response
			Toast toast = Toast.makeText(this, "Server Error Recieving Data",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

	public void attemptSend() {
		doRequest();
	}

	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
