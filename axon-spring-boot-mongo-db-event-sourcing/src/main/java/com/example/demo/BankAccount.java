package com.example.demo;

import java.io.Serializable;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.AnnotationEventListenerAdapter;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventListener;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.spring.config.EventHandlerRegistrar;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import com.example.demo.command.CloseAccountCommand;
import com.example.demo.command.CreateAccountCommand;
import com.example.demo.command.DepositMoneyCommand;
import com.example.demo.command.WithdrawMoneyCommand;
import com.example.demo.event.AccountClosedEvent;
import com.example.demo.event.AccountCreatedEvent;
import com.example.demo.event.MoneyDepositedEvent;
import com.example.demo.event.MoneyWithdrawnEvent;
import com.mongodb.MongoClient;

@Aggregate
public class BankAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@AggregateIdentifier
	private String id;

	private double balance;

	private String owner;

	@CommandHandler
	public BankAccount(CreateAccountCommand command) {
		String id = command.id;
		String name = command.accountCreator;

		Assert.hasLength(id, "Missin id");
		Assert.hasLength(name, "Missig account creator");
		

		System.out.println("CreateAccountCommand wird ausgeführt: ID=" + id + " owner=" + name  );
		AggregateLifecycle.apply(new AccountCreatedEvent(id, name, 0));
	}

	public BankAccount() {
		// TODO Auto-generated constructor stub
	}

	@EventSourcingHandler
	protected void on(AccountCreatedEvent event) {
		this.id = event.id;
		this.owner = event.accountCreator;
		this.balance = event.balance;
		System.out.println("on AccountCreatedEvent wurde ausgeführt: ID=" + id + " owner=" + owner + " Balance=" + balance );
	}
	
	@CommandHandler
	protected void on(CloseAccountCommand command) {
		System.out.println("on CloseAccountCommand wird ausgeführt: ID=" + id);
		AggregateLifecycle.apply(new AccountClosedEvent(id));
	}
	
	@EventSourcingHandler
	protected void on(AccountClosedEvent event) {
		AggregateLifecycle.markDeleted();
	}

	@CommandHandler
	protected void on(DepositMoneyCommand command) {
		double amount = command.amount;

		Assert.isTrue(amount > 0.0, "Deposit must be a positiv number.");

		System.out.println("on DepositMoneyCommand wird ausgeführt: ID=" + id + " amount: " + amount);

		AggregateLifecycle.apply(new MoneyDepositedEvent(id, amount));
	}

	@EventSourcingHandler
	protected void on(MoneyDepositedEvent event) {
		this.balance += event.amount;
		System.out.println("on MoneyDepositedEvent wurde ausgeführt: ID=" + id + " neue balance= " + balance);		
	}

	@CommandHandler
	protected void on(WithdrawMoneyCommand command) {
		double amount = command.amount;

		Assert.isTrue(amount > 0.0, "Withdraw must be a positiv number.");

		if(balance - amount < 0) {
		    throw new InsufficientBalanceException("Insufficient balance.");
		}
		System.out.println("on WithdrawMoneyCommand wird ausgeführt: ID=" + id + " amount: " + amount);

		AggregateLifecycle.apply(new MoneyWithdrawnEvent(id, amount));
	}

	public static class InsufficientBalanceException extends RuntimeException {
        	InsufficientBalanceException(String message) {
			super(message);
		}
	}

	@EventSourcingHandler
	protected void on(MoneyWithdrawnEvent event) {
		this.balance -= event.amount;
		System.out.println("on MoneyWithdrawnEvent wurde ausgeführt: ID=" + id + " neue balance=" + balance);	
	}
	
	@Bean
	public EventStorageEngine eventStore(MongoClient client) {
		return new MongoEventStorageEngine(new DefaultMongoTemplate(client));
	}
}