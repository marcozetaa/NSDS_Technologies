package com.Ex5;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.faultTolerance.counter.CounterActor;
import com.faultTolerance.counter.DataMessage;
import scala.sys.Prop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Main5 {

	public static final int FAULTS = 1;

	private static final int numThreads = 10;

	public static void main(String[] args) throws InterruptedException, TimeoutException {
		scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, SECONDS);

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef supervisor = sys.actorOf(ServerSupervisor5.props(),"supervisor");

		ActorRef server;

		scala.concurrent.Future<Object> waitingForServer =  ask(supervisor, Props.create(Server5.class), 5000);
		server = (ActorRef) waitingForServer.result(timeout,null);

		final ActorRef client = sys.actorOf(Client5.props(), "client");

		// Send messages from multiple threads in parallel
		final ExecutorService exec = Executors.newFixedThreadPool(numThreads);

		//Tell the client who is the server
		client.tell(new ConfigMsg5(server),ActorRef.noSender());

		//POST Messages
		client.tell(new PutMsg5("marco.zanghieri@gmail.com","marco"), ActorRef.noSender());
		client.tell(new PutMsg5("rita.smirnova@hotmail.it","rita"), ActorRef.noSender());

		client.tell(new PutMsg5("none!","Fail!"), ActorRef.noSender());

		client.tell(new PutMsg5("martina.zanghieri@mail.org","martina"), ActorRef.noSender());

		//GET Messages
		client.tell(new GetMsg5("marco"),ActorRef.noSender());
		client.tell(new GetMsg5("rita"),ActorRef.noSender());
		client.tell(new GetMsg5("martina"),ActorRef.noSender());


		exec.shutdown();
		sys.terminate();

	}

}
