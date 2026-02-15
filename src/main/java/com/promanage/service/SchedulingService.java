package com.promanage.service;

import com.promanage.dao.ProjectDAO;
import com.promanage.dao.ProjectDAOImpl;
import com.promanage.model.Project;
import com.promanage.model.ScheduleResult;
import com.promanage.model.ScheduledProject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SchedulingService {

    private final ProjectDAO projectDAO;
    private static final int MAX_DAYS = 5; // Monday to Friday
    private static final String[] DAY_NAMES = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public SchedulingService() {
        this.projectDAO = new ProjectDAOImpl();
    }

    public ScheduleResult generateSchedule() throws SQLException {
        List<Project> pendingProjects = projectDAO.getPendingProjects();

        if (pendingProjects.isEmpty()) {
            return new ScheduleResult(new ArrayList<>(), new ArrayList<>());
        }

        Project[] schedule = new Project[MAX_DAYS];

        List<Project> scheduledProjects = new ArrayList<>();
        List<Project> unscheduledProjects = new ArrayList<>();

        for (Project project : pendingProjects) {
            int deadline = project.getDeadline();

            int scheduledDay = findLatestAvailableSlot(schedule, deadline);

            if (scheduledDay != -1) {
                schedule[scheduledDay] = project;
                scheduledProjects.add(project);

                projectDAO.updateProjectStatus(project.getProjectId(), "SCHEDULED");
            } else {
                unscheduledProjects.add(project);
            }
        }

        List<ScheduledProject> finalSchedule = new ArrayList<>();
        for (int day = 0; day < MAX_DAYS; day++) {
            if (schedule[day] != null) {
                finalSchedule.add(new ScheduledProject(schedule[day], day + 1, DAY_NAMES[day]));
            }
        }

        return new ScheduleResult(finalSchedule, unscheduledProjects);
    }


    private int findLatestAvailableSlot(Project[] schedule, int deadline) {
        for (int day = deadline - 1; day >= 0; day--) {
            if (schedule[day] == null) {
                return day; // Found available slot
            }
        }
        return -1;
    }

    public BigDecimal calculateTotalRevenue(List<ScheduledProject> scheduledProjects) {
        BigDecimal total = BigDecimal.ZERO;
        for (ScheduledProject sp : scheduledProjects) {
            total = total.add(sp.getProject().getRevenue());
        }
        return total;
    }


    public BigDecimal calculateLostRevenue(List<Project> unscheduledProjects) {
        BigDecimal lost = BigDecimal.ZERO;
        for (Project p : unscheduledProjects) {
            lost = lost.add(p.getRevenue());
        }
        return lost;
    }

    public void resetAllProjectStatus() throws SQLException {
        List<Project> allProjects = projectDAO.getAllProjects();
        for (Project project : allProjects) {
            if ("SCHEDULED".equals(project.getStatus())) {
                projectDAO.updateProjectStatus(project.getProjectId(), "PENDING");
            }
        }
        System.out.println("✓ All project statuses reset to PENDING");
    }
}