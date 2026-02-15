package com.promanage.dao;

import com.promanage.model.Project;
import com.promanage.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAOImpl implements ProjectDAO {

    @Override
    public boolean addProject(Project project) throws SQLException {

        String sql = "INSERT INTO projects (title, deadline, revenue, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, project.getTitle());
            pstmt.setInt(2, project.getDeadline());
            pstmt.setBigDecimal(3, project.getRevenue());
            pstmt.setString(4, project.getStatus());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 1) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        project.setProjectId(rs.getInt(1));
                    }
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error adding project: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Project> getAllProjects() throws SQLException {

        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY project_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all projects: " + e.getMessage());
            throw e;
        }

        return projects;
    }

    @Override
    public List<Project> getPendingProjects() throws SQLException {

        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = 'PENDING' ORDER BY revenue DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving pending projects: " + e.getMessage());
            throw e;
        }

        return projects;
    }

    @Override
    public Project getProjectById(int projectId) throws SQLException {

        String sql = "SELECT * FROM projects WHERE project_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractProjectFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving project by ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    @Override
    public boolean updateProjectStatus(int projectId, String status) throws SQLException {

        String sql = "UPDATE projects SET status = ? WHERE project_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, projectId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error updating project status: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean clearAllProjects() throws SQLException {

        String sql = "DELETE FROM projects";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " projects deleted.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing projects: " + e.getMessage());
            throw e;
        }
    }

    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {

        int projectId = rs.getInt("project_id");
        String title = rs.getString("title");
        int deadline = rs.getInt("deadline");
        BigDecimal revenue = rs.getBigDecimal("revenue");
        String status = rs.getString("status");
        Timestamp timestamp = rs.getTimestamp("created_at");

        LocalDateTime createdAt =
                (timestamp != null) ? timestamp.toLocalDateTime() : null;

        return new Project(projectId, title, deadline, revenue, status, createdAt);
    }
}
