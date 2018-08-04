package com.tiaa.exception;

public class AccountingException extends Exception{

	private static final long serialVersionUID = 1L;

	public AccountingException() {

	}

	public AccountingException(Throwable t) {
		super(t);
	}

	public AccountingException(String message) {

		super(message);
	}

	public AccountingException(String message, Throwable t) {
		super(message, t);
	}
}
