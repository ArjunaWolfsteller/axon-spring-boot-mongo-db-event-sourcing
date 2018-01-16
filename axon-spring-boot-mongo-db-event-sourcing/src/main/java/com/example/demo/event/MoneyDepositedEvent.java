package com.example.demo.event;

public class MoneyDepositedEvent {
	public final String id;
	public final double amount;
 
	public MoneyDepositedEvent(String id, double amount) {
		this.id = id;
		this.amount = amount;
		System.out.println("Event: Account " + id + " wurde " + amount + "hinzugefuegt.");
	}
}