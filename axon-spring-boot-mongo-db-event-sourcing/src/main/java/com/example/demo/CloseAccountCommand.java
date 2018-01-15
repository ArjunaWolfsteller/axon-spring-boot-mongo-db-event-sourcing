package com.example.demo;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class CloseAccountCommand {
	@TargetAggregateIdentifier
	public final String id; 
      
	public CloseAccountCommand(String id) {
		System.out.println("CloseAccountCommand wird ausgeführt: ID=" + id);
		this.id = id;
	}
}