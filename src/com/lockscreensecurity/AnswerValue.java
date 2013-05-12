package com.lockscreensecurity;

import java.io.Serializable;

public class AnswerValue implements Serializable {
	private static final long serialVersionUID = -4304473319455825507L;
	public int question_id;
	public int answer_id;
	public String answer_string;
	
	public AnswerValue(int qID, int aID) {
		question_id = qID;
		answer_id = aID;
		answer_string = "";
	}
	
	public AnswerValue(int qID, String aS) {
		question_id = qID;
		answer_id = -1;
		answer_string = aS;
	}
}
