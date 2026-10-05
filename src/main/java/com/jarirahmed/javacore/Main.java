package com.jarirahmed.javacore;

import com.jarirahmed.javacore.advanced.ConcurrencyExamples;
import com.jarirahmed.javacore.basics.BasicsAndControlFlow;
import com.jarirahmed.javacore.collections.CollectionExamples;
import com.jarirahmed.javacore.core.CoreApiExamples;
import com.jarirahmed.javacore.exceptions.EmployeeNotFoundException;
import com.jarirahmed.javacore.functional.FunctionalExamples;
import com.jarirahmed.javacore.generics.GenericExamples;
import com.jarirahmed.javacore.oop.ConsoleNotificationSender;
import com.jarirahmed.javacore.oop.Employee;
import com.jarirahmed.javacore.oop.EmployeeDirectory;
import com.jarirahmed.javacore.oop.FullTimeEmployee;
import com.jarirahmed.javacore.oop.PartTimeEmployee;
import com.jarirahmed.javacore.oop.PayrollService;
import com.jarirahmed.hibernate.HibernateLesson;
import com.jarirahmed.jpa.JpaLesson;
import com.jarirahmed.springtransactions.SpringTransactionLesson;
import com.jarirahmed.springsecurity.demo.SpringSecurityLesson;
import com.jarirahmed.testing.TestingLesson;
import com.jarirahmed.production.ProductionAdvancedLesson;
import com.jarirahmed.projects.employeecli.EmployeeManagementCli;
import com.jarirahmed.projects.inventory.demo.InventoryLesson;
import com.jarirahmed.projects.blog.BlogProjectApplication;
import com.jarirahmed.projects.storefront.StorefrontApplication;
import com.jarirahmed.springapi.demo.SpringApiLesson;
import com.jarirahmed.springcore.demo.SpringCoreLesson;
import com.jarirahmed.springmvc.demo.SpringMvcLesson;
import com.jarirahmed.springboot.SpringBootLearningApplication;
import com.jarirahmed.springboot.SpringBootLesson;
import com.jarirahmed.sqlfoundation.SqlFoundationLesson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Runs one small example from each Phase 1 lesson. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "--project1".equals(args[0])) {
            EmployeeManagementCli.launch(
                    System.in,
                    System.out,
                    args.length > 1 ? java.nio.file.Path.of(args[1]) : java.nio.file.Path.of("employees.txt"));
            return;
        }
        if (args.length > 0 && "--project2".equals(args[0])) {
            InventoryLesson.run();
            return;
        }
        if (args.length > 0 && "--project3".equals(args[0])) {
            BlogProjectApplication.main(Arrays.copyOfRange(args, 1, args.length));
            return;
        }
        if (args.length > 0 && "--project4".equals(args[0])) {
            StorefrontApplication.main(Arrays.copyOfRange(args, 1, args.length));
            return;
        }
        if (args.length > 0 && "--boot".equals(args[0])) {
            SpringBootLearningApplication.main(Arrays.copyOfRange(args, 1, args.length));
            return;
        }

        System.out.println("=== PHASE 1: JAVA FOUNDATION ===");

        System.out.println("\n--- LESSON 1: Types, operators, control flow, and loops ---");
        System.out.println("Types: " + BasicsAndControlFlow.describeCommonTypes());
        System.out.println("Input: " + BasicsAndControlFlow.formatInput("  Jarir  "));
        System.out.println("Arithmetic: " + BasicsAndControlFlow.calculateArithmetic(10, 3));
        System.out.println(BasicsAndControlFlow.describeResult(85));
        System.out.println("Switch: " + BasicsAndControlFlow.describeEmploymentStatus(
                com.jarirahmed.javacore.oop.EmploymentStatus.ACTIVE));
        System.out.println("For-loop sum: " + BasicsAndControlFlow.sumUsingForLoop(1, 5));
        int[] countdown = BasicsAndControlFlow.countdown(3);
        System.out.println("While-loop countdown: " + java.util.Arrays.toString(countdown));
        System.out.println("Array range: " + java.util.Arrays.toString(BasicsAndControlFlow.middleValues()));

        System.out.println("\n--- LESSON 2: Classes, inheritance, interfaces, and composition ---");
        Employee alice = new FullTimeEmployee(1, "Alice", new BigDecimal("5000.00"));
        Employee bob = new PartTimeEmployee(2, "Bob", new BigDecimal("25.00"), 80);
        EmployeeDirectory directory = new EmployeeDirectory();
        directory.add(alice);
        directory.add(bob);
        PayrollService payroll = new PayrollService(new ConsoleNotificationSender());
        System.out.println(alice.summary());
        System.out.println("Alice pay: " + payroll.calculatePay(alice));
        System.out.println(payroll.notifyPayProcessed(alice));

        System.out.println("\n--- LESSON 3: Core APIs ---");
        System.out.println(CoreApiExamples.buildWelcomeMessage("Jarir", 1));
        System.out.println("Parsed id: " + CoreApiExamples.parseEmployeeId(" 42 "));
        System.out.println("Date: " + CoreApiExamples.formatDate(LocalDate.of(2026, 10, 5)));
        System.out.println("Username valid: " + CoreApiExamples.isValidUsername("jarir_2026"));
        System.out.println("Clamped score: " + CoreApiExamples.clamp(120, 0, 100));

        System.out.println("\n--- LESSON 4: Collections ---");
        System.out.println("Queue: " + CollectionExamples.processQueue(List.of("first", "second", "third")));
        System.out.println("Stack: " + CollectionExamples.unwindStack(List.of("first", "second", "third")));
        System.out.println("Skills: " + CollectionExamples.uniqueSortedSkills(
                List.of("Java", "SQL", "Java", "Maven")));
        System.out.println("Names by id: " + CollectionExamples.namesById(List.of(alice, bob)));

        System.out.println("\n--- LESSON 5: Exceptions ---");
        try {
            directory.findById(99);
        } catch (EmployeeNotFoundException exception) {
            System.out.println("Handled checked exception: " + exception.getMessage());
        }
        System.out.println("Safe division: " +
                com.jarirahmed.javacore.exceptions.ExceptionExamples.divideWithCleanup(10, 0, () ->
                        System.out.println("Cleanup ran")));

        System.out.println("\n--- LESSON 6: Generics ---");
        GenericExamples.Box<String> box = new GenericExamples.Box<>("type-safe value");
        System.out.println("Box: " + box.value());
        System.out.println("Greatest: " + GenericExamples.greatest(10, 20));
        List<Integer> ids = new ArrayList<>();
        GenericExamples.addDefaultIds(ids, 3);
        System.out.println("Generic ids: " + ids);

        System.out.println("\n--- LESSON 7: Lambdas, streams, and Optional ---");
        System.out.println("Active names: " + FunctionalExamples.activeEmployeeNames(List.of(alice, bob)));
        System.out.println("Total pay: " + FunctionalExamples.totalMonthlyPay(List.of(alice, bob)));
        System.out.println("Formatted: " + FunctionalExamples.formatEmployee(
                alice, employee -> employee.getId() + " / " + employee.getName()));

        System.out.println("\n--- LESSON 8: Threads, executors, and CompletableFuture ---");
        System.out.println("Parallel counter: " + ConcurrencyExamples.runParallelCounter(3, 100));
        System.out.println("Async employee: " +
                ConcurrencyExamples.loadEmployeeLabelAsync(alice).get());

        System.out.println("\nPHASE 1 COMPLETE");
        SpringCoreLesson.run();
        SpringMvcLesson.run();
        SpringBootLesson.run();
        SqlFoundationLesson.run();
        HibernateLesson.run();
        JpaLesson.run();
        SpringTransactionLesson.run();
        SpringApiLesson.run();
        SpringSecurityLesson.run();
        TestingLesson.run();
        ProductionAdvancedLesson.run();
        System.out.println("\nPHASES 1-12 COMPLETE");
    }
}
