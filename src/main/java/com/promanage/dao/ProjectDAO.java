package com.promanage.dao;

import com.promanage.model.Project;
import java.sql.SQLException;
import java.util.List;


public interface ProjectDAO {


    boolean addProject(Project project) throws SQLException;

    List<Project> getAllProjects() throws SQLException;

    List<Project> getPendingProjects() throws SQLException;

    Project getProjectById(int projectId) throws SQLException;

    boolean updateProjectStatus(int projectId, String status) throws SQLException;

    boolean clearAllProjects() throws SQLException;

}