package com.lab.evaluation22;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class PublisherActor extends AbstractActor {

    private ActorRef broker;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ConfigMsg.class,this::configure).
                match(PublishMsg.class,this::publishMessage).build();
    }

    void publishMessage(PublishMsg msg){
        System.out.println("[PUBLISHER]: Publish event " + msg.getTopic() + "to the broker...");
        broker.tell(msg,self());
    }

    //Configure broker address
    void configure(ConfigMsg msg){
        System.out.println("[PUBLISHER]: Configuring broker...");
        this.broker = msg.getActorRef();
    }

    static Props props() {
        return Props.create(PublisherActor.class);
    }
}
