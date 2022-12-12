package com.lab.evaluation22;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;

public class BrokerActor extends AbstractActorWithStash {

    private ActorRef oddWorker = null;
    private ActorRef evenWorker = null;

    @Override
    public Receive createReceive() {
        return stashOff();
    }

    private final Receive stashOn(){
        return receiveBuilder().match(ConfigMsg.class,this::configure).
                match(SubscribeMsg.class,this::forwardSubscription).
                match(PublishMsg.class,this::stashEvent).
                match(BatchMsg.class,this::flipOff).build();
    }

    private final Receive stashOff(){
        return receiveBuilder().match(ConfigMsg.class,this::configure).
                match(SubscribeMsg.class,this::forwardSubscription).
                match(PublishMsg.class,this::forwardEvent).
                match(BatchMsg.class,this::flipOn).build();
    }


    // Broker change its state to stashOn()
    void flipOn(BatchMsg msg){
        System.out.println("[BROKER] Turning stash on...");
        getContext().become(stashOn());
    }

    // Broker change its state to stashOff()
    void flipOff(BatchMsg msg){
        System.out.println("[BROKER]: Turning stash off...");
        getContext().become(stashOff());
        unstashAll();
    }

    // Broker in state stashOff() stash events
    void stashEvent(PublishMsg msg){
        System.out.println("[BROKER]: Stashing event : "+ msg.getValue());
        stash();
    }

    // Broker forward event published to both workers
    void forwardEvent(PublishMsg msg){
        System.out.println("[BROKER]: Sending event : "+ msg.getValue() +" to workers");
        evenWorker.tell(msg,self());
        oddWorker.tell(msg,self());
    }

    // Broker forward subscription message to worker according to message's key
    void forwardSubscription(SubscribeMsg msg){
        if (msg.getKey() % 2 == 0){ //key is even, forward to evenWorker
            System.out.println("[BROKER]: Sending subscription "+ msg.getTopic() + " to evenWorker...");
            evenWorker.tell(msg,self());
        } else{ //key is odd, forward to oddWorker
            System.out.println("[BROKER]: Sending subscription "+ msg.getTopic() + " to oddWorker...");
            oddWorker.tell(msg,self());
        }
    }

    // Configure workers
    void configure(ConfigMsg msg){
        if( oddWorker == null ) {
            System.out.println("[BROKER]: Configuring oddWorker...");
            this.oddWorker = msg.getActorRef();
        } else {
            System.out.println("[BROKER]: Configuring evenWorker...");
            this.evenWorker = msg.getActorRef();
        }
    }

    static Props props() {
        return Props.create(BrokerActor.class);
    }
}
