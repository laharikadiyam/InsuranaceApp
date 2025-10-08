package com.insurance.app.auth.exceptionHandling;

public class WrongStatusException extends RuntimeException{
	public WrongStatusException(String message) {
		super(message);
	}
}
