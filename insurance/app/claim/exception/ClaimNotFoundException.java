package com.insurance.app.claim.exception;

public class ClaimNotFoundException extends RuntimeException  {
	public ClaimNotFoundException(String message) {
		super(message);
	}

}
