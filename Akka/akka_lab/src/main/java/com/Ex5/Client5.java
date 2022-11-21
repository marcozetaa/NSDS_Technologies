package com.Ex5;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;

public class Client5 extends AbstractActor {

    private ActorRef server;
    private scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, TimeUnit.SECONDS);

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ConfigMsg5.class,this::configure).
                match(PutMsg5.class,this::addMessage).match(GetMsg5.class,this::askMessage).build();
    }

    void addMessage(PutMsg5 msg){
        System.out.println("CLIENT: Add message to server:  " + msg.getName() + ", email: " + msg.getEmail());
        server.tell(msg,self());
    }

    void askMessage(GetMsg5 msg){
        System.out.println("CLIENT: asking server  "+ msg.getName() + " and get replied");
        scala.concurrent.Future<Object> waitingForReply = ask(server,msg,5000);
        try {
            ReplyMsg5 reply = (ReplyMsg5) waitingForReply.result(timeout,null);
            if(reply.getAddress()!=null){
                System.out.println("CLIENT: Received reply, email is " + reply.getAddress());
            }
            else {
                System.out.println("CLIENT: Received reply, no email found!");
            }
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void processReply(ReplyMsg5 msg){
        System.out.println("CLIENT: Unsolicited reply, this should not happen!");
    }

    void configure(ConfigMsg5 msg){
        System.out.println("CLIENT: Received configuration message");
        server = msg.getServerRef();
    }

    static Props props() {
        return Props.create(Client5.class);
    }
}
