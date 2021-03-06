package com.example.demo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.*;
import org.springframework.web.bind.annotation.*;

import com.example.demo.command.CloseAccountCommand;
import com.example.demo.command.CreateAccountCommand;
import com.example.demo.command.DepositMoneyCommand;
import com.example.demo.command.WithdrawMoneyCommand;

import org.springframework.http.HttpStatus;

@RequestMapping("/accounts")
@RestController
public class AccountApi {

	private final CommandGateway commandGateway;

	public AccountApi(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@PostMapping
	public CompletableFuture<String> createAccount(@RequestBody AccountOwner user) {
		String id = UUID.randomUUID().toString();
		System.out.println("erstellte ID: " + id.toString());
		CompletableFuture<String> temp = commandGateway.send(new CreateAccountCommand(id, user.name));
		System.out.println("wichtig: " + temp.toString());		//das sind asyncrone Ergebnisse.... Hä? Gucke hier Junge: http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html
		return temp;
	}

	static class AccountOwner {
		public String name;
	}

	@PutMapping(path = "{accountId}/balance")
	public CompletableFuture<String> deposit(@RequestBody double amount, @PathVariable String accountId) {
		if (amount > 0) {
			System.out.println("REST:Account "+ accountId +" wird um " + amount + " erhoeht.");
			return commandGateway.send(new DepositMoneyCommand(accountId, amount));
		} else {
			System.out.println("REST:Account "+ accountId +" wird um " + amount + " erniedrigt.");
			return commandGateway.send(new WithdrawMoneyCommand(accountId, -amount));
		}
	}

	@DeleteMapping("{id}")
	public CompletableFuture<String> delete(@PathVariable String id) {
		System.out.println("Rest Account "+ id +" wird geschlossen");
		return commandGateway.send(new CloseAccountCommand(id));
	}

	@ExceptionHandler(AggregateNotFoundException.class)	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void notFound() {
	}

	@ExceptionHandler(BankAccount.InsufficientBalanceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String insufficientBalance(BankAccount.InsufficientBalanceException exception) {
		return exception.getMessage();
	}

}