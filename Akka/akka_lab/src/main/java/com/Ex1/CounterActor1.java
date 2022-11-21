package com.Ex1;


import akka.actor.AbstractActor;
import akka.actor.Props;

public class CounterActor1 extends AbstractActor {

    private int counter;

    public CounterActor1() {
        this.counter = 0;
    }

    //parte 1
    /*@Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(IncrementMessage1.class, this::onIncrement)
                .match(DecrementMessage1.class, this::onDecrement)
                .build();
    }

    void onIncrement(IncrementMessage1 msg) {
        ++counter;
        System.out.println("Counter increased to " + counter);
    }

    void onDecrement(DecrementMessage1 msg) {
        --counter;
        System.out.println("Counter increased to " + counter);
    }*/

    //parte 2
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StateMessage1.class, this::onIncrement)
                .build();
    }

    void onIncrement(StateMessage1 msg) {
        if(msg.getCode()==Counter1.INCREMENT){
            ++counter;
            System.out.println("Counter increased to " + counter);
        } else if (msg.getCode()==Counter1.DECREMENT){
            --counter;
            System.out.println("Counter increased to " + counter);
        }
    }

    static Props props() {
        return Props.create(CounterActor1.class);
    }

}
