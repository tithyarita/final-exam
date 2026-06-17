package com.example.demo.repository;

import com.example.demo.entity.Profile;
import com.example.demo.enums.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUniqueId(String uniqueId);

    Optional<Profile> findByEmail(String email);

    List<Profile> findByProfileType(ProfileType profileType);

    List<Profile> findByActive(boolean active);

    List<Profile> findByDepartment(String department);

    boolean existsByUniqueId(String uniqueId);

    boolean existsByEmail(String email);
}