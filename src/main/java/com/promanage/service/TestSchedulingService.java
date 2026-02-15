package com.promanage.service;

import com.promanage.model.ScheduledProject;
import com.promanage.model.ScheduleResult;
import com.promanage.model.Project;
import com.promanage.utils.DatabaseConnection;

import java.math.BigDecimal;


public class TestSchedulingService {

    public static void main(String[] args) {
        SchedulingService schedulingService = new SchedulingService();

        try {
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║     PROMANAGE AUTOMATED SCHEDULING SYSTEM             ║");
            System.out.println("╚════════════════════════════════════════════════════════╝\n");

            // Reset all projects to PENDING before testing
            System.out.println("► Resetting all project statuses...");
            schedulingService.resetAllProjectStatus();
            System.out.println();

            // Generate schedule
            System.out.println("► Generating optimal weekly schedule...\n");
            ScheduleResult result = schedulingService.generateSchedule();

            // Display scheduled projects
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("        📅 WEEKLY SCHEDULE (Monday - Friday)");
            System.out.println("═══════════════════════════════════════════════════════\n");

            if (result.getScheduledProjects().isEmpty()) {
                System.out.println("No projects scheduled.");
            } else {
                for (ScheduledProject sp : result.getScheduledProjects()) {
                    System.out.println(sp);
                }
            }

            // Display unscheduled projects
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("        ⚠️  UNSCHEDULED PROJECTS");
            System.out.println("═══════════════════════════════════════════════════════\n");

            if (result.getUnscheduledProjects().isEmpty()) {
                System.out.println("✓ All projects scheduled successfully!");
            } else {
                System.out.println("The following projects could not be scheduled:\n");
                for (Project p : result.getUnscheduledProjects()) {
                    System.out.println("  - " + p.getTitle() +
                            " [Revenue: ₹" + p.getRevenue() +
                            ", Deadline: " + p.getDeadline() + " days]");
                }
            }

            // Display financial summary
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("        💰 FINANCIAL SUMMARY");
            System.out.println("═══════════════════════════════════════════════════════\n");

            BigDecimal totalRevenue = schedulingService.calculateTotalRevenue(result.getScheduledProjects());
            BigDecimal lostRevenue = schedulingService.calculateLostRevenue(result.getUnscheduledProjects());

            System.out.println("Total Projects Scheduled : " + result.getScheduledProjects().size());
            System.out.println("Projects Not Scheduled   : " + result.getUnscheduledProjects().size());
            System.out.println("Expected Revenue         : ₹" + String.format("%,.2f", totalRevenue));
            System.out.println("Lost Revenue             : ₹" + String.format("%,.2f", lostRevenue));

            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║     ✓ SCHEDULE GENERATION COMPLETED SUCCESSFULLY      ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}