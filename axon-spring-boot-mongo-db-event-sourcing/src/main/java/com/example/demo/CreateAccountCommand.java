package com.example.demo;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class CreateAccountCommand {
	@TargetAggregateIdentifier
	public final String id;
	public final String accountCreator;

	public CreateAccountCommand(String id, String accountCreator) {
		System.out.println("CreateAccountCommand wird ausgeführt: ID=" + id);
		this.id = id;
		this.accountCreator = accountCreator;
	}
}