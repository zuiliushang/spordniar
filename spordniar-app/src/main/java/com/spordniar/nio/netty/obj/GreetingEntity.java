package com.spordniar.nio.netty.obj;

public class GreetingEntity {

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GreetingEntity(String message) {
		super();
		this.message = message;
	}

	public GreetingEntity() {
		super();
	}

	@Override
	public String toString() {
		return "GreetingEntity [message=" + message + "]";
	}
	
}
