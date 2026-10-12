package com.tourism.package_service.config;

import com.tourism.package_service.model.TravelPackage;
import com.tourism.package_service.repository.TravelPackageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner seed(TravelPackageRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(TravelPackage.builder()
                        .destination("Goa")
                        .title("Goa Beach Escape")
                        .description("3 days of beaches, sightseeing and relaxation.")
                        .days(3).nights(2).price(8999)
                        .hotel("Sea Breeze Hotel")
                        .transportIncluded(true)
                        .sightseeingIncluded(true).build());

                repo.save(TravelPackage.builder()
                        .destination("Manali")
                        .title("Manali Mountain Adventure")
                        .description("Mountain views, local sightseeing and adventure.")
                        .days(4).nights(3).price(11999)
                        .hotel("Himalayan Stay")
                        .transportIncluded(true)
                        .sightseeingIncluded(true).build());

                repo.save(TravelPackage.builder()
                        .destination("Kerala")
                        .title("Kerala Backwater Tour")
                        .description("A relaxing Kerala experience with backwater sightseeing.")
                        .days(4).nights(3).price(10999)
                        .hotel("Palm Grove Resort")
                        .transportIncluded(false)
                        .sightseeingIncluded(true).build());
            }
        };
    }
}
