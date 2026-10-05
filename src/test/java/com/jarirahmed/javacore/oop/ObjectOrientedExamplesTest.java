package com.jarirahmed.javacore.oop;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectOrientedExamplesTest {
    @Test
    void subclasses_provide_polymorphic_pay_calculations() {
        Employee fullTime = new FullTimeEmployee(1, "Alice", new BigDecimal("5000.00"));
        Employee partTime = new PartTimeEmployee(2, "Bob", new BigDecimal("25.00"), 80);

        assertEquals(new BigDecimal("5000.00"), fullTime.monthlyPay());
        assertEquals(new BigDecimal("2000.00"), partTime.monthlyPay());
        assertTrue(fullTime.isActive());
    }

    @Test
    void encapsulation_controls_state_and_composition_uses_interface() {
        Employee employee = new FullTimeEmployee(1, "Alice", new BigDecimal("5000.00"));
        employee.rename(" Alice Updated ");
        employee.placeOnLeave();

        assertEquals("Alice Updated", employee.getName());
        assertEquals(EmploymentStatus.ON_LEAVE, employee.getStatus());
        assertFalse(employee.isActive());

        PayrollService payroll = new PayrollService(message -> "captured: " + message);
        assertTrue(payroll.notifyPayProcessed(employee).startsWith("captured: Pay processed"));
    }

    @Test
    void directory_finds_and_sorts_employees() throws Exception {
        EmployeeDirectory directory = new EmployeeDirectory();
        Employee bob = new FullTimeEmployee(2, "Bob", new BigDecimal("3000"));
        Employee alice = new FullTimeEmployee(1, "Alice", new BigDecimal("4000"));
        directory.add(bob);
        directory.add(alice);

        assertEquals("Alice", directory.activeEmployees().getFirst().getName());
        assertEquals(1, directory.findById(1).getId());
        assertThrows(IllegalArgumentException.class, () -> directory.add(alice));
    }
}
