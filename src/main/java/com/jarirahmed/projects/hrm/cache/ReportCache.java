package com.jarirahmed.projects.hrm.cache;

import com.jarirahmed.projects.hrm.dto.PayrollReportResponse;

import java.util.Optional;

/** Small cache abstraction so the service is not coupled to a cache vendor. */
public interface ReportCache {
    Optional<PayrollReportResponse> get(String key);

    void put(String key, PayrollReportResponse report);

    void evict(String key);
}
