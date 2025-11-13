package com.example.perkmanager.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("alice: " + encoder.encode("password1"));
        System.out.println("bob: " + encoder.encode("password2"));
        System.out.println("charlie: " + encoder.encode("password3"));
        System.out.println("dana: " + encoder.encode("password4"));
        System.out.println("evan: " + encoder.encode("password5"));
    }
}
