package com.dna.calendo.config.auth.dto;

import java.util.List;

public class ProjectRequest {

    private boolean accepted;  // 초대 수락 여부 (true = 수락, false = 거절)
    private String projectName;      // 프로젝트 이름
    private List<String> members;    // 팀원 목록 (닉네임 또는 ID)

    // 기본 생성자
    public ProjectRequest() {}

    // Getter & Setter
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
}
