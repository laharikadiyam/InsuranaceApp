package com.insurance.app.catalog.exception;

public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException(String message) {
    	super(message);
    }
}
