package com.tourism.package_service.repository;

import com.tourism.package_service.model.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {}
