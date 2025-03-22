package com.saguro.rapid.configserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saguro.rapid.configserver.entity.GitRepository;

public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long> {
    List<GitRepository> findByEnabledTrueAndOrganizationAndApplication(
        String organization, String application);
}

