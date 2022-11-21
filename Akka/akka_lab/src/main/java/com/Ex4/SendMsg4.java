package com.Ex4;

import akka.actor.ActorRef;

public class SendMsg4 extends Msg4{

    private ActorRef sender;
    private String message;

    public SendMsg4(String value, ActorRef sender) {
        this.sender = sender;
        this.message = value;
    }

    public SendMsg4(String msg){
        this.message = msg;
        this.sender = null;
    }

    public ActorRef getSender() {
        return sender;
    }

    public void setSender(ActorRef sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
}
