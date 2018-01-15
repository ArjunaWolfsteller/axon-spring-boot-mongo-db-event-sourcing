package com.example.demo;

public class MoneyWithdrawnEvent {
	public final String id;
	public final double amount;
 
	public MoneyWithdrawnEvent(String id, double amount) {
		this.id = id;
		this.amount = amount;
		System.out.println("Event: Account " + id + " wurde " + amount + " abgeholben.");
	}
}