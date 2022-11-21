package com.Ex5;

public class GetMsg5 {

    //In receive of this message you read the address of a contact given his name

    private String name;

    public GetMsg5(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
