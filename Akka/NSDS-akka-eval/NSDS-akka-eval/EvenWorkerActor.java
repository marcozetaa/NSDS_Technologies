package com.lab.evaluation22;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.HashMap;

public class EvenWorkerActor extends AbstractActor {

    private final HashMap<String, ActorRef> subscriber;


    public EvenWorkerActor() {
        this.subscriber = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(SubscribeMsg.class,this::addSubscription).
                match(PublishMsg.class,this::notifySubscriber).build();
    }

    // Worker notifies the subscriber related to the topic, otherwise throws an exception
    void notifySubscriber(PublishMsg msg) throws Exception {
        if ( subscriber.containsKey(msg.getTopic()) ){
            System.out.println("[EVEN_WORKER]: Notifying subscriber event of topic "+ msg.getTopic());
            subscriber.get(msg.getTopic()).tell(new NotifyMsg(msg.getValue()),ActorRef.noSender());
        } else {
            System.out.println("[EVEN_WORKER]: Topic "+ msg.getTopic() +" not found, throwing FAULT!");
            throw new Exception("[EVEN_WORKER]: Topic "+ msg.getTopic() + " not found!");
        }
    }

    // Worker add to its list the actor subscribed to the topic, in case the topic does not exist
    // a new topic key is created and added to the list
    void addSubscription(SubscribeMsg msg){
        System.out.println("[EVEN_WORKER]: Adding subscription of topic "+ msg.getTopic());
        subscriber.put(msg.getTopic(),msg.getSender());
    }

    static Props props() {
        return Props.create(EvenWorkerActor.class);
    }
}
