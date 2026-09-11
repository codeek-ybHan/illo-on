package com.illoon.project;

import com.illoon.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    /** 사용자가 멤버로 속한 프로젝트 (최신순) */
    @Query("""
            select p from Project p
            where p.id in (
                select m.id.projectId from ProjectMember m where m.id.userId = :userId
            )
            order by p.createdAt desc
            """)
    List<Project> findAllByMember(Long userId);
}
