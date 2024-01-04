package com.example.backend.Exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){super(message);}
}