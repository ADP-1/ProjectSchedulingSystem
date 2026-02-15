package com.promanage.dao;

import com.promanage.model.Project;
import com.promanage.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.util.List;


public class TestProjectDAO {

    public static void main(String[] args) {
        ProjectDAO projectDAO = new ProjectDAOImpl();

        try {
            System.out.println("=== Testing ProjectDAO Operations ===\n");

            // Test 1: Get all projects
            System.out.println("1. Testing getAllProjects():");
            List<Project> allProjects = projectDAO.getAllProjects();
            System.out.println("   Total projects: " + allProjects.size());

            // Test 2: Add a new project
            System.out.println("\n2. Testing addProject():");
            Project newProject = new Project(
                    "Test Project - Inventory System",
                    4,
                    new BigDecimal("22000.00")
            );

            boolean added = projectDAO.addProject(newProject);
            if (added) {
                System.out.println("   ✓ Project added successfully!");
                System.out.println("   Generated ID: " + newProject.getProjectId());
            }

            // Test 3: Get project by ID
            System.out.println("\n3. Testing getProjectById():");
            Project retrieved = projectDAO.getProjectById(newProject.getProjectId());
            if (retrieved != null) {
                System.out.println("   ✓ Project retrieved: " + retrieved);
            }

            // Test 4: Get pending projects
            System.out.println("\n4. Testing getPendingProjects():");
            List<Project> pendingProjects = projectDAO.getPendingProjects();
            System.out.println("   Pending projects: " + pendingProjects.size());
            for (Project p : pendingProjects) {
                System.out.println("   - " + p.getTitle() + " (Revenue: ₹" + p.getRevenue() + ")");
            }

            // Test 5: Update project status
            System.out.println("\n5. Testing updateProjectStatus():");
            boolean updated = projectDAO.updateProjectStatus(newProject.getProjectId(), "SCHEDULED");
            if (updated) {
                System.out.println("   ✓ Status updated to SCHEDULED");
                Project updatedProject = projectDAO.getProjectById(newProject.getProjectId());
                System.out.println("   Current status: " + updatedProject.getStatus());
            }

            System.out.println("\n=== All DAO Tests Completed Successfully! ===");

        } catch (Exception e) {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}