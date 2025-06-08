package com.saguro.rapid.configserver.repository;

import com.saguro.rapid.configserver.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Query("SELECT o FROM Organization o JOIN FETCH o.applications WHERE o.id = :id")
    Organization findOrganizationWithApplications(@Param("id") Long id);
}