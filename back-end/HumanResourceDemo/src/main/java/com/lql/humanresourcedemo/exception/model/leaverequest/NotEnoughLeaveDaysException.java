package com.lql.humanresourcedemo.exception.model.leaverequest;

public class NotEnoughLeaveDaysException extends LeaveRequestException{
    public NotEnoughLeaveDaysException(String message) {
        super(message);
    }
}
