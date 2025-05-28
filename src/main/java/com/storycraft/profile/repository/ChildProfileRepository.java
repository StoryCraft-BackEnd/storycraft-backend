package com.storycraft.profile.repository;

import com.storycraft.profile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    List<ChildProfile> findByUserEmail(String email);
}
