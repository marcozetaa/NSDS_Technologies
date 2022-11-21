package com.Ex3;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class Server3 extends AbstractActor {

    private Map<String,String> list;

    public Server3() {
        this.list = new HashMap<String,String>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetMsg3.class,this::generateReply).
                match(PutMsg3.class,this::putMessage).build();
    }

    void generateReply(GetMsg3 msg){
        System.out.println("Server got query for name " + msg.getName());
        String email = list.get(msg.getName());
        sender().tell(new ReplyMsg3(msg.getName(),email),self());
    }

    //Adding new contact to the list
    void putMessage(PutMsg3 msg){
        System.out.println("Server got new entry Name:" + msg.getName() + " Address: " + msg.getEmail() );
        this.list.put(msg.getName(),msg.getEmail());
    }

    static Props props() {
        return Props.create(Server3.class);
    }

}
