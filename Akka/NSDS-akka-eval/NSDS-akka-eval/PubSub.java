package com.lab.evaluation22;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import static akka.pattern.Patterns.ask;

public class PubSub {

	public final static String TOPIC0 = "topic0";
	public final static String TOPIC1 = "topic1";

	public static void main(String[] args)  {

		final ActorSystem sys = ActorSystem.create("System");

		scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5,TimeUnit.SECONDS);

		final ActorRef broker = sys.actorOf(BrokerActor.props(),"broker");
		final ActorRef subscriber = sys.actorOf(SubscriberActor.props(),"subscriber");
		final ActorRef publisher = sys.actorOf(PublisherActor.props(),"publisher");

		// create supervisor for workers
		final ActorRef supervisor = sys.actorOf(WorkerSupervisorActor.props(),"supervisor");

		//ask supervisor to create workers
		scala.concurrent.Future<Object> waitingForEvenReply = ask(supervisor, Props.create(EvenWorkerActor.class),5000);
		ActorRef evenWorker = null;
		try {
			evenWorker = (ActorRef) waitingForEvenReply.result(timeout,null);
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}

		scala.concurrent.Future<Object> waitingForOddReply = ask(supervisor, Props.create(OddWorkerActor.class),5000);
		ActorRef oddWorker = null;
		try {
			oddWorker = (ActorRef) waitingForOddReply.result(timeout,null);
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}

		//Configure actors
		subscriber.tell(new ConfigMsg(broker),ActorRef.noSender());
		publisher.tell(new ConfigMsg(broker),ActorRef.noSender());

		broker.tell(new ConfigMsg(oddWorker), ActorRef.noSender());
		broker.tell(new ConfigMsg(evenWorker),ActorRef.noSender());


		// Some example subscriptions
		subscriber.tell(new SubscribeMsg(TOPIC0, subscriber), ActorRef.noSender());
		subscriber.tell(new SubscribeMsg(TOPIC1, subscriber), ActorRef.noSender());

		// Waiting for subscriptions to propagate
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Some example events
		publisher.tell(new PublishMsg(TOPIC0, "Test event 1"), ActorRef.noSender());
		publisher.tell(new PublishMsg(TOPIC1, "Test event 2"), ActorRef.noSender());

		// Waiting for events to propagate
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Turn the broker in batching mode
		broker.tell(new BatchMsg(true), ActorRef.noSender());

		// Waiting for messages to propagate
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// More example events
		publisher.tell(new PublishMsg(TOPIC0, "Test message 3"), ActorRef.noSender());
		publisher.tell(new PublishMsg(TOPIC1, "Test message 4"), ActorRef.noSender());

		// Waiting for events to propagate
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		broker.tell(new BatchMsg(false), ActorRef.noSender());
		// In this example, the last two events shall not be processed until after this point

		// Wait for all messages to be sent and received
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sys.terminate();
	}

}
