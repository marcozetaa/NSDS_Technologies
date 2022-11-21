package com.Ex3;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;

public class Client3 extends AbstractActor {

    private ActorRef server;
    private scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, TimeUnit.SECONDS);

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ConfigMsg3.class,this::configure).
                match(PutMsg3.class,this::addMessage).match(GetMsg3.class,this::askMessage).build();
    }

    void addMessage(PutMsg3 msg){
        System.out.println("Client got query to send a message to server with name " + msg.getName() + " and email: " + msg.getEmail());
        server.tell(msg,self());
    }

    void askMessage(GetMsg3 msg){
        System.out.println("Client got query to ask the server message name: "+ msg.getName() + " and get replied");
        scala.concurrent.Future<Object> waitingForReply = ask(server,msg,10000);
        try {
            ReplyMsg3 reply = (ReplyMsg3) waitingForReply.result(timeout,null);
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

    void processReply(ReplyMsg3 msg){
        System.out.println("CLIENT: Unsolicited reply, this should not happen!");
    }

    void configure(ConfigMsg3 msg){
        System.out.println("CLIENT: Received configuration message");
        server = msg.getServerRef();
    }

    static Props props() {
        return Props.create(Client3.class);
    }
}
