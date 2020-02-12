package com.example;

/**
 * Interface for bank service. This class is used to pay the employee from a Bank.
 * @author demigorgan
 *
 */
public interface Bank {
	
	/**
	 * When invoked, this method pays the specified amount of money to the person with 
	 * specified ID.
	 */
	void pay(String id, double amount);
}
