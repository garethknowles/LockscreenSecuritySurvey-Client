package com.lockscreensecurity;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StringEntryQuestion extends Question implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_string_question);

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
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.next_button) {
			EditText answerEdit = (EditText) findViewById(R.id.stringEntryText);
			String answerVal = answerEdit.getText().toString();
			if (answerVal == null || answerVal.length() == 0) {
				// No Value Selected, Throw Error
				return;
			}
			if (answersArray == null)
				answersArray = new ArrayList<AnswerValue>();
			
			AnswerValue av = new AnswerValue(question_id, answerVal);
			answersArray.add(av);
			super.startNewQuestion();
		} else {
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

	    View v = getCurrentFocus();
	    boolean ret = super.dispatchTouchEvent(event);

	    if (v instanceof EditText) {
	        View w = getCurrentFocus();
	        int scrcoords[] = new int[2];
	        w.getLocationOnScreen(scrcoords);
	        float x = event.getRawX() + w.getLeft() - scrcoords[0];
	        float y = event.getRawY() + w.getTop() - scrcoords[1];

	        //Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
	        if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) { 

	            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	        }
	    }
	return ret;
	}
}