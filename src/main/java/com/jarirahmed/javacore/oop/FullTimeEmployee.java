package com.jarirahmed.javacore.oop;

import java.math.BigDecimal;

/** A full-time employee receives a fixed monthly salary. */
public final class FullTimeEmployee extends Employee {
    private final BigDecimal monthlySalary;

    public FullTimeEmployee(int id, String name, BigDecimal monthlySalary) {
        super(id, name);
        if (monthlySalary == null || monthlySalary.signum() < 0) {
            throw new IllegalArgumentException("Monthly salary cannot be negative.");
        }
        this.monthlySalary = monthlySalary;
    }

    @Override
    public BigDecimal monthlyPay() {
        return monthlySalary;
    }
}
