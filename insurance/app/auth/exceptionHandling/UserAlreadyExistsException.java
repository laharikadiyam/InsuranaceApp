package com.insurance.app.auth.exceptionHandling;

public class UserAlreadyExistsException extends RuntimeException{
	public UserAlreadyExistsException(String message) {
		super(message);
	}

}
