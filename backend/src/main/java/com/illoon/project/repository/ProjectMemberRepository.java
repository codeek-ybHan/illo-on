package com.illoon.project.repository;

import com.illoon.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMember.Pk> {

    Optional<ProjectMember> findByIdProjectIdAndIdUserId(Long projectId, Long userId);

    boolean existsByIdProjectIdAndIdUserId(Long projectId, Long userId);

    List<ProjectMember> findAllByIdProjectId(Long projectId);

    long countByIdProjectId(Long projectId);
}
