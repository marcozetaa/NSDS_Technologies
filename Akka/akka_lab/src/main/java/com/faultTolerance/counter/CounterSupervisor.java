package com.faultTolerance.counter;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class CounterSupervisor {

	public static final int NORMAL_OP = 0;
	public static final int FAULT_OP = -1; //emulates a fault happening

	public static final int FAULTS = 1;

	public static void main(String[] args) {
		scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, SECONDS);

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef supervisor = sys.actorOf(CounterSupervisorActor.props(), "supervisor");

		ActorRef counter;
		try {
			
			// Asks the supervisor to create the child actor and returns a reference
			scala.concurrent.Future<Object> waitingForCounter = ask(supervisor, Props.create(CounterActor.class), 5000);
			counter = (ActorRef) waitingForCounter.result(timeout, null);

			counter.tell(new DataMessage(NORMAL_OP), ActorRef.noSender());

			for (int i = 0; i < FAULTS; i++)
				counter.tell(new DataMessage(FAULT_OP), ActorRef.noSender());

			counter.tell(new DataMessage(NORMAL_OP), ActorRef.noSender());
			//if we execute this flow, receiver gets a normal message and shows counter=1, it's restarted because
			//it exceeds the max #failures in a minute, then the receives this last message, shows counter=1 and terminates

			/*
			If instead we set the SupervisorStrategy to resume(), the state is not reset
			So when it resumes, it gets the second message but the state was restored at 1 -> counter=2
			*/

			/*
			If instead we set the SupervisorStrategy to stop(), the actor is not restarted
			So the second message (the one after the fault) is not delivered to the actor
			 */

			sys.terminate();

		} catch (TimeoutException | InterruptedException e1) {
		
			e1.printStackTrace();
		}

	}

}
