package com.Ex3;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class CounterActor3 extends AbstractActor {

	private int counter;

	public CounterActor3() {
		this.counter = 0;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(GetMsg3.class, this::onMessage).
															match(PutMsg3.class, this::onOtherMessage).build();
	}

	void onOtherMessage(PutMsg3 msg) {
		System.out.println("Received other type of message");
	}

	void onMessage(GetMsg3 msg) {
		++counter;
		System.out.println("Counter increased to " + counter);
	}

	static Props props() {
		return Props.create(CounterActor3.class);
	}

}
