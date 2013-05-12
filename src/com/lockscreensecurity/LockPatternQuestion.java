package com.lockscreensecurity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LockPatternQuestion extends Question implements OnClickListener {

	public static final String PREFS_NAME = "LockScreenQuestions";
	int question_id = 0;
	int question_num = 0;
	private static final int _ReqCreatePattern = 0;
	String pattern;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pattern_question);

		try {
			TextView question_text = (TextView) findViewById(R.id.question_text_view);
			String questionString = question.getString("question_text");
			question_text.setText(questionString);
			question_id = question.getInt("question_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Button button = (Button) findViewById(R.id.next_button);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.pattern_button);
		button.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case _ReqCreatePattern:
			if (resultCode == RESULT_OK) {
				pattern = data.getStringExtra(LockPatternActivity._Pattern);
				if (pattern == null || pattern.length() == 0) {
					// No Value Selected, Throw Error
					return;
				}
				if (answersArray == null)
					answersArray = new ArrayList<AnswerValue>();
				AnswerValue av = new AnswerValue(question_id, pattern);
				answersArray.add(av);
				super.startNewQuestion();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.pattern_button) {
			Intent intent = new Intent(this, LockPatternActivity.class);
			intent.putExtra(LockPatternActivity._Mode,
					LockPatternActivity.LPMode.CreatePattern);
			startActivityForResult(intent, _ReqCreatePattern);
		} else if (id == R.id.next_button) {
			if (pattern == null || pattern.length() == 0) {
				// No Value Selected, Throw Error
				return;
			}
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			AnswerValue av = new AnswerValue(question_id, pattern);
			answersArray.add(av);
			super.startNewQuestion();
		} else {
		}
	}
}
