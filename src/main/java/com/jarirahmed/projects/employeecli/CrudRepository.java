package com.jarirahmed.projects.employeecli;

import java.util.List;
import java.util.Optional;

/** Generic repository contract: the id type is not hard-coded into the API. */
public interface CrudRepository<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);

    void save(T value);

    void deleteById(ID id);

    void deleteAll();
}
