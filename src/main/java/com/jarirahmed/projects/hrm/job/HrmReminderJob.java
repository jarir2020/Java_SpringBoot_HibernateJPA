package com.jarirahmed.projects.hrm.job;

import com.jarirahmed.projects.hrm.dto.JobResponse;
import com.jarirahmed.projects.hrm.service.HrmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Scheduled work calls the same service use case exposed by the demo button. */
@Component
public class HrmReminderJob {
    private static final Logger log = LoggerFactory.getLogger(HrmReminderJob.class);
    private final HrmService hrmService;

    public HrmReminderJob(HrmService hrmService) {
        this.hrmService = hrmService;
    }

    @Scheduled(fixedDelayString = "${project5.jobs.reminder-delay-ms:3600000}")
    public void queueLeaveReminders() {
        JobResponse result = hrmService.queueLeaveReminders("scheduled-job");
        if (result.queued() > 0) {
            log.info("Project 5 scheduled job queued {} leave reminders", result.queued());
        }
    }
}
