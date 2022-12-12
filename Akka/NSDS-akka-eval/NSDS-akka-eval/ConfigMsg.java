package com.lab.evaluation22;

import akka.actor.ActorRef;

public class ConfigMsg {

    private ActorRef address;

    public ConfigMsg(ActorRef address) {
        this.address = address;
    }

    ActorRef getActorRef(){
        return this.address;
    }

}
