package com.dna.calendo.service;

import com.dna.calendo.config.auth.dto.ProjectDTO;
import com.dna.calendo.domain.project.Project;
import com.dna.calendo.google.User;
import com.dna.calendo.repository.ProjectRepository;
import com.dna.calendo.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectDTO> getProjectsForUser(String nickName) {
        // 닉네임으로 사용자 조회
        User user = userRepository.findByNickName(nickName)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + nickName));

        // 사용자 ID로 속한 프로젝트 조회
        return projectRepository.findProjectsByUserId(user.getUserId())
                .stream()
                .map(project -> new ProjectDTO(project.getProjectName()))
                .collect(Collectors.toList());
    }

    public Optional<Project> findById(Long projectId) {
        return projectRepository.findById(projectId);
    }
}
