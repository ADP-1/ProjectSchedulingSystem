package com.promanage.model;

import java.util.List;

public class ScheduleResult {
    private final List<ScheduledProject> scheduledProjects;
    private final List<Project> unscheduledProjects;

    public ScheduleResult(List<ScheduledProject> scheduledProjects, List<Project> unscheduledProjects) {
        this.scheduledProjects = scheduledProjects;
        this.unscheduledProjects = unscheduledProjects;
    }

    public List<ScheduledProject> getScheduledProjects() {
        return scheduledProjects;
    }

    public List<Project> getUnscheduledProjects() {
        return unscheduledProjects;
    }

    public int getTotalScheduled() {
        return scheduledProjects.size();
    }

    public int getTotalUnscheduled() {
        return unscheduledProjects.size();
    }

    public boolean hasUnscheduledProjects() {
        return !unscheduledProjects.isEmpty();
    }
}