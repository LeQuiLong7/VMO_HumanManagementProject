package com.lql.humanresourcedemo.exception.model;

public class FileNotSupportException extends RuntimeException{
    public FileNotSupportException(String extension) {
        super("Not support " + extension + " file for avatar");
    }
}
