package com.illoon.project.repository;

import com.illoon.project.domain.ProjectInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Long> {

    Optional<ProjectInvite> findByToken(String token);
}
