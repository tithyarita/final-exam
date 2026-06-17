package com.example.demo.model;

import com.example.demo.entity.Profile;
import com.example.demo.enums.ProfileType;

public class ProfileBuilder {

    private final Profile.ProfileBuilder builder;

    public ProfileBuilder() {
        this.builder = Profile.builder();
    }

    public ProfileBuilder withFirstName(String firstName) {
        builder.firstName(firstName);
        return this;
    }

    public ProfileBuilder withLastName(String lastName) {
        builder.lastName(lastName);
        return this;
    }

    public ProfileBuilder withEmail(String email) {
        builder.email(email);
        return this;
    }

    public ProfileBuilder withPhone(String phone) {
        builder.phone(phone);
        return this;
    }

    public ProfileBuilder withDepartment(String department) {
        builder.department(department);
        return this;
    }

    public ProfileBuilder withAddress(String address) {
        builder.address(address);
        return this;
    }

    public ProfileBuilder withPhoto(byte[] photo) {
        builder.photo(photo);
        return this;
    }

    public ProfileBuilder withProfileType(ProfileType profileType) {
        builder.profileType(profileType);
        return this;
    }

    public ProfileBuilder withUniqueId(String uniqueId) {
        builder.uniqueId(uniqueId);
        return this;
    }

    public ProfileBuilder withActive(boolean active) {
        builder.active(active);
        return this;
    }

    public Profile build() {
        return builder.build();
    }

    public static Profile createDefaultStudentProfile() {
        return Profile.builder()
                .active(true)
                .profileType(ProfileType.STUDENT)
                .build();
    }

    public static Profile createDefaultEmployeeProfile() {
        return Profile.builder()
                .active(true)
                .profileType(ProfileType.EMPLOYEE)
                .build();
    }

    public static Profile createDefaultUserProfile() {
        return Profile.builder()
                .active(true)
                .profileType(ProfileType.USER)
                .build();
    }
}