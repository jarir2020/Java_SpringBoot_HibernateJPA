package com.jarirahmed.javacore.oop;

import java.math.BigDecimal;

/** A part-time employee is paid from an hourly rate and monthly hours. */
public final class PartTimeEmployee extends Employee {
    private final BigDecimal hourlyRate;
    private final int monthlyHours;

    public PartTimeEmployee(int id, String name, BigDecimal hourlyRate, int monthlyHours) {
        super(id, name);
        if (hourlyRate == null || hourlyRate.signum() < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative.");
        }
        if (monthlyHours < 0) {
            throw new IllegalArgumentException("Monthly hours cannot be negative.");
        }
        this.hourlyRate = hourlyRate;
        this.monthlyHours = monthlyHours;
    }

    @Override
    public BigDecimal monthlyPay() {
        return hourlyRate.multiply(BigDecimal.valueOf(monthlyHours));
    }
}
