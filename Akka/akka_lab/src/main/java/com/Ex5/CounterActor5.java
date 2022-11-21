package com.Ex5;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class CounterActor5 extends AbstractActor {

	private int counter;

	public CounterActor5() {
		this.counter = 0;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(GetMsg5.class, this::onMessage).
															match(PutMsg5.class, this::onOtherMessage).build();
	}

	void onOtherMessage(PutMsg5 msg) {
		System.out.println("Received other type of message");
	}

	void onMessage(GetMsg5 msg) {
		++counter;
		System.out.println("Counter increased to " + counter);
	}

	static Props props() {
		return Props.create(CounterActor5.class);
	}

}
