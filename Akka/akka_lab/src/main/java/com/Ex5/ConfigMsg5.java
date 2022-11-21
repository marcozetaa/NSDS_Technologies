package com.Ex5;

import akka.actor.ActorRef;

public class ConfigMsg5 {

    private ActorRef address;

    public ConfigMsg5(ActorRef address) {
        this.address = address;
    }

    ActorRef getServerRef(){
        return this.address;
    }

}
