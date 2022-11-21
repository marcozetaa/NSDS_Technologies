package com.Ex4;

import akka.actor.ActorRef;

public class ConfigMsg4 extends Msg4{

    private ActorRef address;

    public ConfigMsg4(ActorRef address) {
        this.address = address;
    }

    ActorRef getServerRef(){
        return this.address;
    }

}
