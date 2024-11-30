package com.example.kpi.exceptions;

public class ResourceExistException extends Throwable {
    public ResourceExistException(String userAlreadyHave) {
        super(userAlreadyHave);
    }
}
