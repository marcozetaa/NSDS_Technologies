package com.Ex4;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main4 {

	private static final int numThreads = 10;
	private static final int numMessages = 3;


	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef server = sys.actorOf(Server4.props(), "server");
		final ActorRef client = sys.actorOf(Client4.props(), "client");

		// Tell the client who is the server
		client.tell(new ConfigMsg4(server), ActorRef.noSender());

		// An example execution
		client.tell(new SendMsg4("Hello Luca!", ActorRef.noSender()), ActorRef.noSender());
		client.tell(new SendMsg4("Hello Alessandro!", ActorRef.noSender()), ActorRef.noSender());

		client.tell(new SleepMsg4(), ActorRef.noSender());

		client.tell(new SendMsg4("You should be sleeping now 1!", ActorRef.noSender()), ActorRef.noSender());
		client.tell(new SendMsg4("You should be sleeping now 2!", ActorRef.noSender()), ActorRef.noSender());

		client.tell(new WakeUpMsg4(), ActorRef.noSender());

		// Tell the client who is the server
		client.tell(new ConfigMsg4(server), ActorRef.noSender());

		// An example execution
		client.tell(new SendMsg4("Hello Luca!", ActorRef.noSender()), ActorRef.noSender());
		client.tell(new SendMsg4("Hello Alessandro!", ActorRef.noSender()), ActorRef.noSender());

		client.tell(new SleepMsg4(), ActorRef.noSender());

		client.tell(new SendMsg4("You should be sleeping now 1!", ActorRef.noSender()), ActorRef.noSender());
		client.tell(new SendMsg4("You should be sleeping now 2!", ActorRef.noSender()), ActorRef.noSender());

		client.tell(new WakeUpMsg4(), ActorRef.noSender());

		// Wait for all messages to be sent and received
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sys.terminate();

	}

}

