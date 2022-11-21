package com.Ex4;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Server4 extends AbstractActorWithStash {

	@Override
	public Receive createReceive() {
		return awake();
	}

	private final Receive awake(){
		return receiveBuilder().match(SendMsg4.class, this:: sendBackText).
				match(SleepMsg4.class,this::goToSleep).build();
	}

	private final Receive sleepy(){
		return receiveBuilder().match(SendMsg4.class,this::putAside).
				match(WakeUpMsg4.class,this::wakeUp).build();
	}

	void goToSleep(SleepMsg4 msg){
		System.out.println("SERVER: Going to sleep...");
		getContext().become(sleepy());
	}

	void sendBackText(SendMsg4 msg){
		System.out.println("SERVER: sending back message to " + msg.getSender() + " TEXT=[" + msg.getMessage() + "]");
		msg.getSender().tell(msg,self());
	}

	void putAside(SendMsg4 msg){
		System.out.println("SERVER: putting message aside...");
		stash();
	}

	void wakeUp(WakeUpMsg4 msg){
		System.out.println("SERVER: Waking up!");
		getContext().become(awake());
		unstashAll();
	}

	static Props props() {
		return Props.create(Server4.class);
	}

}
