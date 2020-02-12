package com.example;

import java.util.List;

public class EmployeeManager {

	private Company employeeRepository;
	private Bank bankService;

	public EmployeeManager(Company employeeRepository, Bank bankService) {
		this.employeeRepository = employeeRepository;
		this.bankService = bankService;
	}

	/**
	 * This method retrieves the list of all employees. And pay
	 * the employees one by one. And returns the number of employees
	 * that has been paid.
	 * @return
	 */
	public int payEmployees() {
		List<Employee> employees = employeeRepository.getAllEmployees();
		int payments = 0;
		for (Employee employee : employees) {
			try {
				bankService.pay(employee.getId(), employee.getSalary());
				employee.setPaid(true);
				payments++;
			} catch (RuntimeException e) {
				employee.setPaid(false);
			}
		}
		return payments;
	}
}
