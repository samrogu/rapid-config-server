package com.saguro.rapid.configserver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saguro.rapid.configserver.entity.GitRepository;
import com.saguro.rapid.configserver.repository.GitRepositoryRepository;

@Service
public class GitRepositoryService {

    private GitRepositoryRepository repository;

    public GitRepositoryService(GitRepositoryRepository repository) {
        this.repository = repository;
    }

    public List<GitRepository> getRepositoriesByOrganizationApp(String organization, String application) {
        return repository.findByEnabledTrueAndOrganizationAndApplication(organization, application);
    }
}