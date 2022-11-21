package com.Ex1;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Counter1 {

    private static final int numThreads = 10;
    private static final int numMessages = 100;

    public static final int INCREMENT = 1;
    public static final int DECREMENT = -1;

    public static void main(String[] args) {

        final ActorSystem sys = ActorSystem.create("System");
        final ActorRef counter = sys.actorOf(CounterActor1.props(), "counter");

        // Send messages from multiple threads in parallel
        final ExecutorService exec = Executors.newFixedThreadPool(numThreads);

        //parte 1
        /*for (int i = 0; i < numMessages; i++) {
            exec.submit(() -> counter.tell(new IncrementMessage1(), ActorRef.noSender()));
            exec.submit(() -> counter.tell(new DecrementMessage1(), ActorRef.noSender()));
        }*/

        //parte 2
        for (int i = 0; i < numMessages; i++) {
            exec.submit(() -> counter.tell(new StateMessage1(Counter1.INCREMENT), ActorRef.noSender()));
            exec.submit(() -> counter.tell(new StateMessage1(Counter1.DECREMENT), ActorRef.noSender()));
        }

        // Wait for all messages to be sent and received
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exec.shutdown();
        sys.terminate();

    }

}