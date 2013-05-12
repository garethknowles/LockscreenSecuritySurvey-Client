package com.lockscreensecurity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class MultiChoiceQuestion extends Question implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ScrollView sv = (ScrollView) findViewById(R.id.main_view);
		sv.fullScroll(ScrollView.FOCUS_UP);
		
		Button button = (Button) findViewById(R.id.next_button);
		button.setOnClickListener(this);
		
		try {

			TextView question_text = (TextView) findViewById(R.id.question_text_view);
			String questionString = question.getString("question_text");
			question_text.setText(questionString);
			question_id = question.getInt("question_id");
			
			RadioGroup answersGroup = (RadioGroup) findViewById(R.id.multi_question_answers);
			for (int i = 0; i < answers.length(); i++) {
				RadioButton b = new RadioButton(this);
				b.setTextSize(20);
				b.setTextColor(Color.parseColor("#1A5A72"));
				JSONObject answer = answers.getJSONObject(i);
				b.setText(answer.getString("answer_text"));
				b.setTag(answer.getInt("answer_id"));
				answersGroup.addView(b);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.next_button) {
			RadioGroup answersGroup = (RadioGroup) findViewById(R.id.multi_question_answers);
			int selected = answersGroup.getCheckedRadioButtonId();
			if (selected == -1) {
				// No Value Selected, Throw Error
				return;
			}
			RadioButton b = (RadioButton) findViewById(selected);
			int answer_id = (Integer) b.getTag();
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			AnswerValue av = new AnswerValue(question_id, answer_id);
			answersArray.add(av);
			super.startNewQuestion();
		} else {
		}
	}
}