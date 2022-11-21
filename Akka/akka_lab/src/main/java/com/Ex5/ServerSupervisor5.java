package com.Ex5;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.typed.internal.AbstractSupervisor;
import akka.japi.pf.DeciderBuilder;
import com.faultTolerance.counter.CounterSupervisorActor;

import java.time.Duration;

public class ServerSupervisor5 extends AbstractActor {

    //strategy
    private static SupervisorStrategy strategy =
            new OneForOneStrategy(
                    1,
                    Duration.ofMinutes(1), DeciderBuilder.match(Exception.class, e -> SupervisorStrategy.resume())
                    .build());

    public SupervisorStrategy supervisorStrategy(){ return strategy; }

    public ServerSupervisor5() {}

    @Override
    public Receive createReceive() {
        // Creates the child actor within the supervisor actor context
        return receiveBuilder()
                .match(
                        Props.class,
                        props -> {
                            getSender().tell(getContext().actorOf(props), getSelf());
                        })
                .build();
    }

    static Props props() {
        return Props.create(CounterSupervisorActor.class);
    }
}
