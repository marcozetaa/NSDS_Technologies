package com.Ex4;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Client4 extends AbstractActor {

	private ActorRef server;
	private scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, SECONDS);


	@Override
	public Receive createReceive() {
		return receiveBuilder().match(ConfigMsg4.class,this::configure).
				match(SendMsg4.class,this::sendText).
				match(WakeUpMsg4.class,this::reroute).
				match(SleepMsg4.class,this::reroute).build();
	}

	void configure(ConfigMsg4 msg){
		System.out.println("CLIENT: configuring server...");
		this.server = msg.getServerRef();
	}

	void  reroute(Msg4 msg){
		System.out.println("CLIENT: Rerouting Message" + msg);
		server.tell(msg,self());
	}

	void sendText(SendMsg4 msg){
		if (msg.getSender() == ActorRef.noSender()){
			System.out.println("CLIENT: Sending message to server TEXT=[" + msg.getMessage() + "]");
			msg.setSender(self());
			server.tell(msg,self());
		}
		else{
			System.out.println("CLIENT: Received Reply TEXT=[" + msg.getMessage() + "]");
		}
	}


	static Props props() {
		return Props.create(Client4.class);
	}

}
