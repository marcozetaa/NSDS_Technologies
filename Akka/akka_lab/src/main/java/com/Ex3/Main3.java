package com.Ex3;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main3 {

	private static final int numThreads = 10;

	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef server = sys.actorOf(Server3.props(), "server");
		final ActorRef client = sys.actorOf(Client3.props(), "client");

		// Send messages from multiple threads in parallel
		final ExecutorService exec = Executors.newFixedThreadPool(numThreads);

		//Tell the client who is the server
		client.tell(new ConfigMsg3(server),ActorRef.noSender());

		//POST Messages
		client.tell(new PutMsg3("gmail","marco"), ActorRef.noSender());
		client.tell(new PutMsg3("hotmail","rita"), ActorRef.noSender());
		client.tell(new PutMsg3("mail","martina"), ActorRef.noSender());


		//GET Messages
		client.tell(new GetMsg3("marco"),ActorRef.noSender());
		client.tell(new GetMsg3("rita"),ActorRef.noSender());
		client.tell(new GetMsg3("martina"),ActorRef.noSender());


		exec.shutdown();
		sys.terminate();

	}

}
