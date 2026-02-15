package com.promanage.model;

public class ScheduledProject {
    private final Project project;
    private final int dayNumber;
    private final String dayName;

    public ScheduledProject(Project project, int dayNumber, String dayName) {
        this.project = project;
        this.dayNumber = dayNumber;
        this.dayName = dayName;
    }

    public Project getProject() {
        return project;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getDayName() {
        return dayName;
    }

    @Override
    public String toString() {
        return String.format("%s (Day %d) - %s [Revenue: ₹%.2f, Deadline: %d days]",
                dayName, dayNumber, project.getTitle(), project.getRevenue(), project.getDeadline());
    }
}