package com.example.demo.service;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;
import com.example.demo.enums.ProfileType;
import com.example.demo.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Override
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
    }

    @Override
    public Profile getProfileByUniqueId(String uniqueId) {
        return profileRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Profile not found with uniqueId: " + uniqueId));
    }

    @Override
    public Profile createProfile(ProfileDTO profileDTO) {
        if (profileRepository.existsByEmail(profileDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + profileDTO.getEmail());
        }

        Profile profile = Profile.builder()
                .uniqueId(generateUniqueId(profileDTO.getProfileType(), profileDTO.getDepartment()))
                .firstName(profileDTO.getFirstName())
                .lastName(profileDTO.getLastName())
                .email(profileDTO.getEmail())
                .phone(profileDTO.getPhone())
                .department(profileDTO.getDepartment())
                .address(profileDTO.getAddress())
                .profileType(profileDTO.getProfileType())
                .active(true)
                .build();

        return profileRepository.save(profile);
    }

    @Override
    public Profile createProfileWithPhoto(ProfileDTO profileDTO, MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            validatePhoto(photo);
        }

        Profile profile = createProfile(profileDTO);
        if (photo != null && !photo.isEmpty()) {
            profile.setPhoto(photo.getBytes());
            profile = profileRepository.save(profile);
        }
        return profile;
    }

    @Override
    public Profile updateProfile(Long id, ProfileDTO profileDTO) {
        Profile existing = getProfileById(id);

        Optional<Profile> emailCheck = profileRepository.findByEmail(profileDTO.getEmail());
        if (emailCheck.isPresent() && !emailCheck.get().getId().equals(id)) {
            throw new RuntimeException("Email already in use: " + profileDTO.getEmail());
        }

        existing.setFirstName(profileDTO.getFirstName());
        existing.setLastName(profileDTO.getLastName());
        existing.setEmail(profileDTO.getEmail());
        existing.setPhone(profileDTO.getPhone());
        existing.setDepartment(profileDTO.getDepartment());
        existing.setAddress(profileDTO.getAddress());
        existing.setProfileType(profileDTO.getProfileType());

        return profileRepository.save(existing);
    }

    @Override
    public Profile updateProfilePhoto(Long id, MultipartFile photo) throws IOException {
        validatePhoto(photo);
        Profile existing = getProfileById(id);
        existing.setPhoto(photo.getBytes());
        return profileRepository.save(existing);
    }

    @Override
    public void deleteProfile(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new RuntimeException("Profile not found with id: " + id);
        }
        profileRepository.deleteById(id);
    }

    @Override
    public List<Profile> getProfilesByType(ProfileType profileType) {
        return profileRepository.findByProfileType(profileType);
    }

    @Override
    public List<Profile> getProfilesByDepartment(String department) {
        return profileRepository.findByDepartment(department);
    }

    @Override
    public String generateUniqueId(ProfileType profileType, String department) {
        String typePrefix = switch (profileType) {
            case STUDENT -> "STU";
            case EMPLOYEE -> "EMP";
            case USER -> "USR";
        };

        String deptCode = (department != null && department.length() >= 3)
                ? department.substring(0, 3).toUpperCase()
                : "GEN";

        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String uuidSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        return String.format("%s-%s-%s-%s", typePrefix, year, deptCode, uuidSuffix);
    }

    private void validatePhoto(MultipartFile photo) {
        String contentType = photo.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new RuntimeException("Only JPEG and PNG images are allowed");
        }

        if (photo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Photo size must be less than 5MB");
        }
    }
}