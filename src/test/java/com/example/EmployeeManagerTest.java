package com.example;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EmployeeManagerTest {

    private Company company;
    private Bank bank;

    private EmployeeManager employeeManager;

    @Spy
    private Employee employee1 = new Employee("1", 1000);

    @Spy
    private Employee employee2 = new Employee("2", 2000);

    @Spy
    private Employee employee3 = new Employee("3", 3000);

    @Before
    public void setup() {
        // Mocks are being created.
        company = mock(Company.class);
        bank = mock(Bank.class);

        // Actual EmployeeManager
        employeeManager = new EmployeeManager(company, bank);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPayEmployeesWhenOneEmployeeIsPresent() {
        when(company.getAllEmployees()).thenReturn(Collections.singletonList(employee1));

        assertThat(employeeManager.payEmployees()).isEqualTo(1);

        verify(bank, times(1)).pay("1", 1000);
    }

    @Test
    public void testPayEmployeesWhenSeveralEmployeesArePresent() {
        // COMPLETE
        when(company.getAllEmployees()).thenReturn(asList(employee1, employee2, employee3));

        assertThat(employeeManager.payEmployees()).isEqualTo(3);

        verify(bank, times(1)).pay("1", 1000);
        verify(bank, times(1)).pay("2", 2000);
        verify(bank, times(1)).pay("3", 3000);
    }

    @Test
    public void testPayEmployeesInOrderWhenSeveralEmployeeArePresent() {
        // an example of invocation order verification
        when(company.getAllEmployees()).thenReturn(asList(employee1, employee2));

        assertThat(employeeManager.payEmployees()).isEqualTo(2);

        InOrder inOrder = inOrder(bank, employee1, employee2);
        inOrder.verify(bank).pay("1", 1000);
        inOrder.verify(bank).pay("2", 2000);

        verifyNoMoreInteractions(bank);
    }

    @Test
    public void testEmployeeSetPaidIsCalledAfterPaying() {
        // COMPLETE
        when(company.getAllEmployees()).thenReturn(Collections.singletonList(employee1));

        assertThat(employeeManager.payEmployees()).isEqualTo(1);

        InOrder inOrder = inOrder(bank, employee1);
        inOrder.verify(bank).pay("1", 1000);
        inOrder.verify(employee1).setPaid(true);
        verifyNoMoreInteractions(bank);

        assertThat(employee1.isPaid()).isTrue();
    }

    @Test
    public void testExampleOfArgumentCaptor() {
        // Just an example of ArgumentCaptor
        when(company.getAllEmployees()).thenReturn(asList(employee1, employee2));

        assertThat(employeeManager.payEmployees()).isEqualTo(2);

        // Argument captors
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);

        verify(bank, times(2)).pay(idCaptor.capture(), amountCaptor.capture());

        assertThat(idCaptor.getAllValues()).containsExactly("1", "2");
        assertThat(amountCaptor.getAllValues()).containsExactly(1000.0, 2000.0);
    }

    @Test
    public void testEmployeeSetPaidOfArgumentCaptor() {
        // COMPLETE
        when(company.getAllEmployees()).thenReturn(asList(employee1, employee2, employee3));

        assertThat(employeeManager.payEmployees()).isEqualTo(3);

        ArgumentCaptor<Boolean> paidCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(employee1, times(1)).setPaid(paidCaptor.capture());
        verify(employee2, times(1)).setPaid(paidCaptor.capture());
        verify(employee3, times(1)).setPaid(paidCaptor.capture());

        assertThat(paidCaptor.getAllValues()).containsExactly(true, true, true);
    }


    @Test
    public void testPayEmployeesWhenBankServiceThrowsException() {
        when(company.getAllEmployees()).thenReturn(Collections.singletonList(employee1));

        doThrow(new RuntimeException()).when(bank).pay(anyString(), anyDouble());

        // number of payments must be 0
        assertThat(employeeManager.payEmployees()).isEqualTo(0);

        // make sure that Employee.paid is updated accordingly
        verify(employee1).setPaid(false);
    }

    /**
     * Test a scenario when paying employee1 causes exception and paying employee2 goes fine.
     */
    @Test
    public void testOtherEmployeesArePaidWhenBankServiceThrowsException() {
        // COMPLETE
        when(company.getAllEmployees()).thenReturn(asList(employee1, employee2));

//        cannot get Id from a spy!
//        doThrow(new RuntimeException()).when(bank).pay(employee1.getId(), anyDouble());

//        Invalid use of argument matchers!
//        2 matchers expected, 1 recorded:
//        doThrow(new RuntimeException()).when(bank).pay("1", anyDouble());

        doThrow(new RuntimeException()).when(bank).pay("1", 1000);

        assertThat(employeeManager.payEmployees()).isEqualTo(1);

        verify(employee1).setPaid(false);
        verify(employee2).setPaid(true);
    }
}