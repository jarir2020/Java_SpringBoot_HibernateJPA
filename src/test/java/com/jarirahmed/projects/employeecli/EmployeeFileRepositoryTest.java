package com.jarirahmed.projects.employeecli;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Exercises NIO persistence and malformed-file handling. */
class EmployeeFileRepositoryTest {
    @Test
    void saves_and_loads_records_using_utf8_text() throws Exception {
        Path file = Files.createTempFile("employees-", ".txt");
        EmployeeFileRepository repository = new EmployeeFileRepository();
        EmployeeRecord alice = new EmployeeRecord(
                1, "Alice Rahman", "Engineering", new BigDecimal("5000.00"));
        alice.deactivate();

        repository.save(file, List.of(alice));
        List<EmployeeRecord> loaded = repository.load(file);

        assertEquals(1, loaded.size());
        assertEquals(alice.id(), loaded.getFirst().id());
        assertEquals(alice.name(), loaded.getFirst().name());
        assertEquals(EmployeeStatus.INACTIVE, loaded.getFirst().status());
    }

    @Test
    void rejects_duplicate_ids_in_a_saved_file() throws Exception {
        Path file = Files.createTempFile("employees-invalid-", ".txt");
        Files.writeString(
                file,
                "id|name|department|monthlySalary|status\n"
                        + "1|Alice|Engineering|5000|ACTIVE\n"
                        + "1|Bob|Operations|3000|ACTIVE\n");

        assertThrows(EmployeeStorageException.class, () -> new EmployeeFileRepository().load(file));
    }
}
