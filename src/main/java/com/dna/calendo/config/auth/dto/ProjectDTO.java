package com.dna.calendo.config.auth.dto;

public class ProjectDTO {
    private String projectName;

    public ProjectDTO(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
