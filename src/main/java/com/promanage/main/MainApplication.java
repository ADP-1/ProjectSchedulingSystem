package com.promanage.main;

import com.promanage.dao.ProjectDAO;
import com.promanage.dao.ProjectDAOImpl;
import com.promanage.model.Project;
import com.promanage.model.ScheduledProject;
import com.promanage.model.ScheduleResult;
import com.promanage.service.SchedulingService;
import com.promanage.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application class with menu-driven interface
 * Entry point for ProManage Task Scheduling System
 */
public class MainApplication {

    private static final ProjectDAO projectDAO = new ProjectDAOImpl();
    private static final SchedulingService schedulingService = new SchedulingService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        displayWelcomeBanner();

        boolean running = true;

        while (running) {
            try {
                displayMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1:
                        addNewProject();
                        break;
                    case 2:
                        viewAllProjects();
                        break;
                    case 3:
                        viewPendingProjects();
                        break;
                    case 4:
                        generateWeeklySchedule();
                        break;
                    case 5:
                        resetProjectStatuses();
                        break;
                    case 6:
                        clearAllProjects();
                        break;
                    case 0:
                        running = false;
                        exitApplication();
                        break;
                    default:
                        System.out.println("\n⚠️  Invalid choice! Please select 0-6.\n");
                }

                if (running && choice != 0) {
                    pressEnterToContinue();
                }

            } catch (Exception e) {
                System.err.println("\n✗ Error: " + e.getMessage());
                scanner.nextLine(); // Clear buffer
                pressEnterToContinue();
            }
        }
    }

    /**
     * Display welcome banner
     */
    private static void displayWelcomeBanner() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║          PROMANAGE TASK SCHEDULING SYSTEM                    ║");
        System.out.println("║          Automated Project Management Solution               ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Display main menu
     */
    private static void displayMenu() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        MAIN MENU                             ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Add New Project                                          ║");
        System.out.println("║  2. View All Projects                                        ║");
        System.out.println("║  3. View Pending Projects                                    ║");
        System.out.println("║  4. Generate Weekly Schedule                                 ║");
        System.out.println("║  5. Reset Project Statuses                                   ║");
        System.out.println("║  6. Clear All Projects                                       ║");
        System.out.println("║  0. Exit                                                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.print("\nEnter your choice: ");
    }

    /**
     * Get user menu choice
     */
    private static int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            return choice;
        } catch (Exception e) {
            scanner.nextLine(); // Clear buffer
            return -1;
        }
    }

    /**
     * Option 1: Add new project
     */
    private static void addNewProject() throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("                     ADD NEW PROJECT");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Get project title
        System.out.print("Enter Project Title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("\n✗ Error: Project title cannot be empty!");
            return;
        }

        // Get deadline
        System.out.print("Enter Deadline (1-5 days): ");
        int deadline;
        try {
            deadline = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (deadline < 1 || deadline > 5) {
                System.out.println("\n✗ Error: Deadline must be between 1 and 5 days!");
                return;
            }
        } catch (Exception e) {
            scanner.nextLine(); // Clear buffer
            System.out.println("\n✗ Error: Invalid deadline! Please enter a number between 1-5.");
            return;
        }

        // Get revenue
        System.out.print("Enter Expected Revenue (₹): ");
        BigDecimal revenue;
        try {
            revenue = scanner.nextBigDecimal();
            scanner.nextLine(); // Consume newline

            if (revenue.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("\n✗ Error: Revenue must be greater than 0!");
                return;
            }
        } catch (Exception e) {
            scanner.nextLine(); // Clear buffer
            System.out.println("\n✗ Error: Invalid revenue! Please enter a valid number.");
            return;
        }

        // Create and add project
        Project project = new Project(title, deadline, revenue);

        if (!project.isValid()) {
            System.out.println("\n✗ Error: Invalid project data!");
            return;
        }

        boolean added = projectDAO.addProject(project);

        if (added) {
            System.out.println("\n✓ Project added successfully!");
            System.out.println("  Project ID: " + project.getProjectId());
            System.out.println("  Title: " + project.getTitle());
            System.out.println("  Deadline: " + project.getDeadline() + " days");
            System.out.println("  Revenue: ₹" + String.format("%,.2f", project.getRevenue()));
        } else {
            System.out.println("\n✗ Failed to add project!");
        }
    }

    /**
     * Option 2: View all projects
     */
    private static void viewAllProjects() throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("                     ALL PROJECTS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        List<Project> projects = projectDAO.getAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects found in the database.");
            return;
        }

        System.out.printf("%-5s %-35s %-10s %-15s %-12s%n",
                "ID", "Title", "Deadline", "Revenue", "Status");
        System.out.println("─────────────────────────────────────────────────────────────────────────────");

        for (Project p : projects) {
            System.out.printf("%-5d %-35s %-10d ₹%-14s %-12s%n",
                    p.getProjectId(),
                    truncate(p.getTitle(), 35),
                    p.getDeadline(),
                    String.format("%,.2f", p.getRevenue()),
                    p.getStatus());
        }

        System.out.println("\nTotal Projects: " + projects.size());
    }

    /**
     * Option 3: View pending projects
     */
    private static void viewPendingProjects() throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("                   PENDING PROJECTS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        List<Project> projects = projectDAO.getPendingProjects();

        if (projects.isEmpty()) {
            System.out.println("No pending projects found.");
            return;
        }

        System.out.printf("%-5s %-35s %-10s %-15s%n",
                "ID", "Title", "Deadline", "Revenue");
        System.out.println("───────────────────────────────────────────────────────────────────────");

        for (Project p : projects) {
            System.out.printf("%-5d %-35s %-10d ₹%-14s%n",
                    p.getProjectId(),
                    truncate(p.getTitle(), 35),
                    p.getDeadline(),
                    String.format("%,.2f", p.getRevenue()));
        }

        System.out.println("\nTotal Pending Projects: " + projects.size());
    }

    /**
     * Option 4: Generate weekly schedule
     */
    private static void generateWeeklySchedule() throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("              GENERATE WEEKLY SCHEDULE");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        System.out.print("⚠️  This will update project statuses. Continue? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("\n✗ Schedule generation cancelled.");
            return;
        }

        System.out.println("\n► Generating optimal schedule...\n");

        ScheduleResult result = schedulingService.generateSchedule();

        // Display scheduled projects
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("        📅 WEEKLY SCHEDULE (Monday - Friday)");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        if (result.getScheduledProjects().isEmpty()) {
            System.out.println("No projects could be scheduled.");
        } else {
            for (ScheduledProject sp : result.getScheduledProjects()) {
                System.out.println("  " + sp);
            }
        }

        // Display unscheduled projects
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("        ⚠️  UNSCHEDULED PROJECTS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        if (result.getUnscheduledProjects().isEmpty()) {
            System.out.println("  ✓ All projects scheduled successfully!");
        } else {
            for (Project p : result.getUnscheduledProjects()) {
                System.out.println("  • " + p.getTitle() +
                        " [Revenue: ₹" + String.format("%,.2f", p.getRevenue()) +
                        ", Deadline: " + p.getDeadline() + " days]");
            }
        }

        // Display financial summary
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("        💰 FINANCIAL SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        BigDecimal totalRevenue = schedulingService.calculateTotalRevenue(result.getScheduledProjects());
        BigDecimal lostRevenue = schedulingService.calculateLostRevenue(result.getUnscheduledProjects());

        System.out.println("  Projects Scheduled   : " + result.getScheduledProjects().size());
        System.out.println("  Projects Unscheduled : " + result.getUnscheduledProjects().size());
        System.out.println("  Expected Revenue     : ₹" + String.format("%,.2f", totalRevenue));
        System.out.println("  Lost Revenue         : ₹" + String.format("%,.2f", lostRevenue));

        System.out.println("\n✓ Schedule generation completed successfully!");
    }

    /**
     * Option 5: Reset project statuses
     */
    private static void resetProjectStatuses() throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("              RESET PROJECT STATUSES");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        System.out.print("⚠️  This will reset all SCHEDULED projects to PENDING. Continue? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("\n✗ Reset cancelled.");
            return;
        }

        schedulingService.resetAllProjectStatus();
        System.out.println("\n✓ All project statuses have been reset to PENDING!");
    }

    /**
     * Option 6: Clear all projects
     */
    private static void clearAllProjects() throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("                 CLEAR ALL PROJECTS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        System.out.print("⚠️  WARNING: This will delete ALL projects permanently! Continue? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
            System.out.println("\n✗ Operation cancelled.");
            return;
        }

        System.out.print("\n⚠️  Are you absolutely sure? Type 'DELETE' to confirm: ");
        String finalConfirmation = scanner.nextLine().trim();

        if (!finalConfirmation.equals("DELETE")) {
            System.out.println("\n✗ Operation cancelled.");
            return;
        }

        boolean cleared = projectDAO.clearAllProjects();

        if (cleared) {
            System.out.println("\n✓ All projects have been deleted from the database!");
        } else {
            System.out.println("\n✗ Failed to clear projects!");
        }
    }

    /**
     * Exit application
     */
    private static void exitApplication() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          Thank you for using ProManage!                      ║");
        System.out.println("║          Closing database connection...                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        DatabaseConnection.closeConnection();
        scanner.close();
        System.out.println("✓ Application closed successfully. Goodbye!\n");
    }

    /**
     * Utility method to truncate long strings
     */
    private static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Utility method to pause and wait for user
     */
    private static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
        System.out.println("\n");
    }
}