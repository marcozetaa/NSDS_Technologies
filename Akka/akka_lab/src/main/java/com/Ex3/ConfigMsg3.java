package com.Ex3;

import akka.actor.ActorRef;

public class ConfigMsg3 {

    private ActorRef address;

    public ConfigMsg3(ActorRef address) {
        this.address = address;
    }

    ActorRef getServerRef(){
        return this.address;
    }

}
