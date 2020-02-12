package com.example;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

public class EmployeeManagerTest {

	private EmployeeManager employeeManager;

	private Company company;

	private Bank bank;

	@Before
	public void setup() {
		//  Mocks are being created.
		company = mock(Company.class);
		bank = mock(Bank.class);
		
		employeeManager = new EmployeeManager(company, bank);
	}
	
	@Test
	public void testPayEmployeesWhenOneEmployeeIsPresent() {
		when(company.getAllEmployees()).thenReturn(asList(new Employee("1", 1000)));
		
		assertThat(employeeManager.payEmployees()).isEqualTo(1);
		verify(bank, times(1)).pay("1", 1000);
	}

	@Test
	public void testPayEmployeesWhenSeveralEmployeesArePresent() {
		// COMPLETE
	}

	@Test
	public void testPayEmployeesInOrderWhenSeveralEmployeeArePresent() {
		// an example of invocation order verification
		when(company.getAllEmployees())
				.thenReturn(asList(
						new Employee("1", 1000),
						new Employee("2", 2000)));
		assertThat(employeeManager.payEmployees()).isEqualTo(2);
		InOrder inOrder = inOrder(bank);
		inOrder.verify(bank).pay("1", 1000);
		inOrder.verify(bank).pay("2", 2000);
		verifyNoMoreInteractions(bank);
	}
	
	@Test
	public void testEmployeeSetPaidIsCalledAfterPaying() {
		// COMPLETE
	}

	@Test
	public void testExampleOfArgumentCaptor() {
		// Just an example of ArgumentCaptor
		when(company.getAllEmployees())
				.thenReturn(asList(
						new Employee("1", 1000),
						new Employee("2", 2000)));
		assertThat(employeeManager.payEmployees()).isEqualTo(2);
		ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

		verify(bank, times(2))
			.pay(idCaptor.capture(), amountCaptor.capture());
		assertThat(idCaptor.getAllValues()).containsExactly("1", "2");
		assertThat(amountCaptor.getAllValues()).containsExactly(1000.0, 2000.0);
		verifyNoMoreInteractions(bank);
	}



	@Test
	public void testPayEmployeesWhenBankServiceThrowsException() {
		Employee employee = spy(new Employee("1", 1000));
		when(company.getAllEmployees())
				.thenReturn(asList(employee));
		doThrow(new RuntimeException())
				.when(bank).pay(anyString(), anyDouble());
		// number of payments must be 0
		assertThat(employeeManager.payEmployees()).isEqualTo(0);
		// make sure that Employee.paid is updated accordingly
		verify(employee).setPaid(false);
	}

	@Test
	public void testOtherEmployeesArePaidWhenBankServiceThrowsException() {
		Employee notToBePaid = spy(new Employee("1", 1000));
		Employee toBePaid = spy(new Employee("2", 2000));
		when(company.getAllEmployees())
			.thenReturn(asList(notToBePaid, toBePaid));
		doThrow(new RuntimeException())
			.doNothing()
			.when(bank).pay(anyString(), anyDouble());
		// number of payments must be 1
		assertThat(employeeManager.payEmployees()).isEqualTo(1);
		// make sure that Employee.paid is updated accordingly
		verify(notToBePaid).setPaid(false);
		verify(toBePaid).setPaid(true);
	}

	@Test
	public void testArgumentMatcherExample() {
		// equivalent to the previous test, with argument matcher
		Employee notToBePaid = spy(new Employee("1", 1000));
		Employee toBePaid = spy(new Employee("2", 2000));
		when(company.getAllEmployees())
			.thenReturn(asList(notToBePaid, toBePaid));
		doThrow(new RuntimeException())
			.when(bank).pay(
					argThat(s -> s.equals("1")),
					anyDouble());
		// number of payments must be 1
		assertThat(employeeManager.payEmployees()).isEqualTo(1);
		// make sure that Employee.paid is updated accordingly
		verify(notToBePaid).setPaid(false);
		verify(toBePaid).setPaid(true);
	}
}