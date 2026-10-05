-- Seed rows are intentionally small enough to inspect by hand in SQL.

INSERT INTO department (id, name) VALUES
    (1, 'Engineering'),
    (2, 'Operations'),
    (3, 'People');

INSERT INTO employee (id, department_id, full_name, email, salary, hired_on) VALUES
    (1, 1, 'Alice Rahman', 'alice@example.com', 9000.00, DATE '2024-01-15'),
    (2, 1, 'Bob Karim', 'bob@example.com', 6500.00, DATE '2024-06-03'),
    (3, 2, 'Carol Islam', 'carol@example.com', 5200.00, DATE '2025-02-10'),
    (4, 3, 'David Hasan', 'david@example.com', 4800.00, DATE '2025-04-21');

INSERT INTO project (id, name, budget) VALUES
    (1, 'Learning Platform', 50000.00),
    (2, 'Internal CRM', 75000.00);

INSERT INTO employee_project (
    employee_id, project_id, assignment_role, hours_per_week, assigned_on
) VALUES
    (1, 1, 'Lead', 20.00, DATE '2025-01-05'),
    (2, 1, 'Contributor', 15.00, DATE '2025-01-12'),
    (2, 2, 'Analyst', 10.00, DATE '2025-03-01'),
    (3, 2, 'Coordinator', 12.00, DATE '2025-03-05');
