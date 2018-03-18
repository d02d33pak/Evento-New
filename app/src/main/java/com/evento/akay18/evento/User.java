package com.evento.akay18.evento;

/**
 * Created by akshaykumar on 04/11/17.
 */

public class User {
    private String name, email, mob;

    public User() {
    }

    public User(String name, String email, String mob) {
        this.name = name;
        this.email = email;
        this.mob = mob;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMob() {
        return mob;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }
}
