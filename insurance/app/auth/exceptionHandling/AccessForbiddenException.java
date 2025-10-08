package com.insurance.app.auth.exceptionHandling;

public class AccessForbiddenException extends RuntimeException{
	public AccessForbiddenException(String message) {
		super(message);
	}

}
