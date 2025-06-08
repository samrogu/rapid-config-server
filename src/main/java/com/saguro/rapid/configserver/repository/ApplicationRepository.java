package com.saguro.rapid.configserver.repository;

import com.saguro.rapid.configserver.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByEnabledTrueAndOrganizationId(Long organizationId);
    List<Application> findByEnabledTrueAndOrganizationIdAndName(Long organizationId, String name);
    List<Application> findByOrganizationId(Long organizationId);

    Optional<Application> findByOrganizationUidAndUidAndLabel(String uidOrg, String uidApp, String label);
}
