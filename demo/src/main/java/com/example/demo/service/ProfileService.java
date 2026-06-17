package com.example.demo.service;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;
import com.example.demo.enums.ProfileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProfileService {

    List<Profile> getAllProfiles();

    Profile getProfileById(Long id);

    Profile getProfileByUniqueId(String uniqueId);

    Profile createProfile(ProfileDTO profileDTO);

    Profile createProfileWithPhoto(ProfileDTO profileDTO, MultipartFile photo) throws IOException;

    Profile updateProfile(Long id, ProfileDTO profileDTO);

    Profile updateProfilePhoto(Long id, MultipartFile photo) throws IOException;

    void deleteProfile(Long id);

    List<Profile> getProfilesByType(ProfileType profileType);

    List<Profile> getProfilesByDepartment(String department);

    String generateUniqueId(ProfileType profileType, String department);
}