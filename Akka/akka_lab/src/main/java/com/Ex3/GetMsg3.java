package com.Ex3;

public class GetMsg3 {

    //In receive of this message you read the address of a contact given his name

    private String name;

    public GetMsg3(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
