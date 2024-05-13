package com.lql.humanresourcedemo.exception.model.project;

public class ProjectException extends RuntimeException{
    public ProjectException(String message) {
        super(message);
    }
    public ProjectException(Long projectId) {
        super("Could not find project " + projectId);
    }
}
