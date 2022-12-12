package com.lab.evaluation22;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class SubscriberActor extends AbstractActor {

    private ActorRef broker;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(NotifyMsg.class,this::getNotification).
                match(ConfigMsg.class,this::configure).
                match(SubscribeMsg.class,this::issueSubscription).build();
    }

    // get Event form subscribed topic
    void getNotification(NotifyMsg msg){
        System.out.println("[SUBSCRIBER]: Received notification with event: " + msg.getValue());
    }

    //Configure broker address
    void configure(ConfigMsg msg){
        System.out.println("[SUBSCRIBER]: Configuring broker...");
        this.broker = msg.getActorRef();
    }

    //Issue Subscription to broker
    void issueSubscription(SubscribeMsg msg){
        System.out.println("[SUBSCRIBER]: Sending subscription to broker of topic " + msg.getTopic());
        broker.tell(msg,self());
    }

    static Props props() {
        return Props.create(SubscriberActor.class);
    }
}
