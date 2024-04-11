package com.lql.humanresourcedemo.exception.model.leaverequest;

public class StatusNotValidException extends LeaveRequestException{
    public StatusNotValidException(String message) {
        super(message);
    }
}
