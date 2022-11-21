package com.faultTolerance.counter;

public class DataMessage {

	//instead of having multiple types of messages use only 1
	//then, based on the actual code (i.e. the state of the message)
	//the receiver will decide what to do
	private int code;
	
	public int getCode() {
		return code;
	}

	public DataMessage(int code) {
		this.code = code;
	}
	
}
