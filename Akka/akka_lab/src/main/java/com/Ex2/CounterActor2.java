package com.Ex2;



import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import com.Ex1.Counter1;
import com.Ex1.StateMessage1;

public class CounterActor2 extends AbstractActorWithStash {

    private int counter;

    public CounterActor2() {
        this.counter = 0;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message2.class, this::onIncrement)
                .build();
    }

    //stash() puts the message in a FIFO stack
    //unstashAll() because there are no guarantees on ordering -> wake them all and check the counter
        //we might have received more increments and are able to do more decrements
    void onIncrement(Message2 msg) {
        if (msg.getCode() == Counter2.INCREMENT) {
            ++counter;
            System.out.println("Counter increased to " + counter);
            unstashAll();
        } else if (msg.getCode() == Counter2.DECREMENT) {
            if (counter > 0) {
                --counter;
                System.out.println("Counter increased to " + counter);
            }
            else stash();
        }
    }

    static Props props() {
        return Props.create(com.Ex2.CounterActor2.class);
    }

}
