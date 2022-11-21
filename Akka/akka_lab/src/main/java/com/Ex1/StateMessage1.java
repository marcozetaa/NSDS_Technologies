package com.Ex1;

public class StateMessage1 {

    //instead of having multiple types of messages use only 1
    //then, based on the actual code (i.e. the state of the message)
    //the receiver will decide what to do
    private int code;

    public int getCode() {
        return code;
    }

    public StateMessage1(int code) {
        this.code = code;
    }

}
