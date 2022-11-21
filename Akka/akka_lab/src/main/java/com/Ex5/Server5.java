package com.Ex5;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class Server5 extends AbstractActor {

    private Map<String,String> list;

    public Server5() {
        System.out.println("Entertin constructor");
        this.list = new HashMap<String,String>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetMsg5.class,this::generateReply).
                match(PutMsg5.class,this::putMessage).build();
    }

    void generateReply(GetMsg5 msg){
        System.out.println("SERVER: Querying " + msg.getName());
        String email = list.get(msg.getName());
        sender().tell(new ReplyMsg5(msg.getName(),email),self());
    }

    //Adding new contact to the list
    void putMessage(PutMsg5 msg) throws Exception {
        if (msg.getName() != "Fail!"){
            System.out.println("SERVER: I am executing a NORMAL operation");
            System.out.println("SERVER: New entry: " + msg.getName() + " Email: " + msg.getEmail() );
            this.list.put(msg.getName(),msg.getEmail());
        } else if (msg.getName() == "Fail!"){
            System.out.println("I am emulating a FAULT");
            throw new Exception("Actor Fault!");
        }
    }

    static Props props() {
        return Props.create(Server5.class);
    }

}
