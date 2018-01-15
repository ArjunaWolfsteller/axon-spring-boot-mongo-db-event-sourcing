package com.example.demo;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class DepositMoneyCommand {
	@TargetAggregateIdentifier
	public final String id;
	public final double amount;

	public DepositMoneyCommand(String id, double amount) {
		this.id = id;
		this.amount = amount;
	}
}