package com.jarirahmed.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests do not need Spring or a database; Mockito replaces the data port. */
@ExtendWith(MockitoExtension.class)
class EmployeeLabelServiceTest {
    @Mock
    private EmployeeDirectoryPort directory;

    @Test
    void formats_a_label_from_the_dependency_result() {
        when(directory.findById(7))
                .thenReturn(Optional.of(new EmployeeSummary(7, "Nadia Karim", "Operations")));

        EmployeeLabelService service = new EmployeeLabelService(directory);

        assertEquals("Nadia Karim [Operations]", service.labelFor(7));
        verify(directory).findById(7);
    }

    @Test
    void reports_a_missing_employee_as_a_domain_failure() {
        when(directory.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeLabelService(directory).labelFor(99));

        assertEquals("Employee 99 was not found.", exception.getMessage());
        verify(directory).findById(99);
    }
}
