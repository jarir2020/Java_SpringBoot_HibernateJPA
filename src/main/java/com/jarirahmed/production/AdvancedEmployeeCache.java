package com.jarirahmed.production;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Cache annotations are applied by a Spring proxy. Calls through that proxy
 * can return a cached value without invoking the backing store again.
 */
@Service
public class AdvancedEmployeeCache {
    private final AdvancedEmployeeStore store;

    public AdvancedEmployeeCache(AdvancedEmployeeStore store) {
        this.store = store;
    }

    @Cacheable(cacheNames = "advancedEmployees", key = "#employeeId")
    public AdvancedEmployee findById(long employeeId) {
        return store.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee " + employeeId + " was not found."));
    }

    @CacheEvict(cacheNames = "advancedEmployees", key = "#employeeId")
    public void evict(long employeeId) {
        // The annotation removes stale data before the next read.
    }

    public int backingStoreLoadCount() {
        return store.loadCount();
    }
}
