package com.Ex3;

public class PutMsg3 {

    //receiving this message you add a new contact in the list
    private String email;
    private String name;

    public PutMsg3(String email,String name) {
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
