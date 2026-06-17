package com.example.demo.controller;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;
import com.example.demo.enums.ProfileType;
import com.example.demo.service.ProfileService;
import com.example.demo.util.BarcodeGenerator;
import com.example.demo.util.PDFGenerator;
import com.example.demo.util.QRCodeGenerator;
import com.example.demo.model.BarcodeType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String listProfiles(Model model) {
        List<Profile> profiles = profileService.getAllProfiles();
        model.addAttribute("profiles", profiles);
        model.addAttribute("profileTypes", ProfileType.values());
        return "profiles/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("profileDTO", new ProfileDTO());
        model.addAttribute("profileTypes", ProfileType.values());
        return "profiles/create";
    }

    @PostMapping("/create")
    public String createProfile(@Valid @ModelAttribute ProfileDTO profileDTO,
                                 @RequestParam(value = "photo", required = false) MultipartFile photo,
                                 RedirectAttributes redirectAttributes) {
        try {
            Profile profile;
            if (photo != null && !photo.isEmpty()) {
                profile = profileService.createProfileWithPhoto(profileDTO, photo);
            } else {
                profile = profileService.createProfile(profileDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Profile created successfully. ID: " + profile.getUniqueId());
            return "redirect:/profiles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating profile: " + e.getMessage());
            return "redirect:/profiles/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Profile profile = profileService.getProfileById(id);
        ProfileDTO dto = ProfileDTO.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .department(profile.getDepartment())
                .address(profile.getAddress())
                .profileType(profile.getProfileType())
                .active(profile.isActive())
                .build();
        model.addAttribute("profileDTO", dto);
        model.addAttribute("profileId", id);
        model.addAttribute("profileTypes", ProfileType.values());
        return "profiles/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProfile(@PathVariable Long id, @Valid @ModelAttribute ProfileDTO profileDTO,
                                 @RequestParam(value = "photo", required = false) MultipartFile photo,
                                 RedirectAttributes redirectAttributes) {
        try {
            profileService.updateProfile(id, profileDTO);
            if (photo != null && !photo.isEmpty()) {
                profileService.updateProfilePhoto(id, photo);
            }
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
            return "redirect:/profiles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
            return "redirect:/profiles/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProfile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            profileService.deleteProfile(id);
            redirectAttributes.addFlashAttribute("success", "Profile deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting profile: " + e.getMessage());
        }
        return "redirect:/profiles";
    }

    @GetMapping("/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {
        Profile profile = profileService.getProfileById(id);
        model.addAttribute("profile", profile);
        return "profiles/view";
    }

    @GetMapping("/{id}/preview")
    public String previewIDCard(@PathVariable Long id, Model model) {
        try {
            Profile profile = profileService.getProfileById(id);

            String photoBase64 = null;
            if (profile.getPhoto() != null && profile.getPhoto().length > 0) {
                photoBase64 = Base64.getEncoder().encodeToString(profile.getPhoto());
            }

            String qrCodeBase64 = null;
            try {
                String qrData = "ID: " + profile.getUniqueId() + "\nName: " + profile.getFirstName() + " " + profile.getLastName();
                qrCodeBase64 = QRCodeGenerator.generateQRCodeBase64(qrData);
            } catch (Exception e) {
                // QR code generation failed, skip
            }

            model.addAttribute("profile", profile);
            model.addAttribute("photoBase64", photoBase64);
            model.addAttribute("qrCodeBase64", qrCodeBase64);
            return "profiles/preview";
        } catch (Exception e) {
            model.addAttribute("error", "Error generating preview: " + e.getMessage());
            return "redirect:/profiles";
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable Long id) {
        try {
            Profile profile = profileService.getProfileById(id);
            byte[] pdfBytes = PDFGenerator.generateIDCardPDF(profile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "id_card_" + profile.getUniqueId() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) {
        try {
            Profile profile = profileService.getProfileById(id);
            String qrData = "ID: " + profile.getUniqueId() + "\nName: " + profile.getFirstName() + " " + profile.getLastName();
            byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(qrData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).body(qrBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/barcode/{type}")
    public ResponseEntity<byte[]> getBarcode(@PathVariable Long id, @PathVariable String type) {
        try {
            Profile profile = profileService.getProfileById(id);
            BarcodeType barcodeType = BarcodeType.valueOf(type.toUpperCase());
            byte[] barcodeBytes = BarcodeGenerator.generateBarcodeImage(profile.getUniqueId(), barcodeType);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).body(barcodeBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/batch")
    public String showBatchForm(Model model) {
        model.addAttribute("profileTypes", ProfileType.values());
        return "profiles/batch";
    }

    @PostMapping("/batch")
    public String batchGenerate(@RequestParam("type") ProfileType type,
                                 @RequestParam("department") String department,
                                 @RequestParam("count") int count,
                                 RedirectAttributes redirectAttributes) {
        try {
            for (int i = 0; i < count; i++) {
                ProfileDTO dto = ProfileDTO.builder()
                        .firstName("Batch")
                        .lastName("User-" + (i + 1))
                        .email("batch" + (i + 1) + "." + System.currentTimeMillis() + "@example.com")
                        .department(department)
                        .profileType(type)
                        .active(true)
                        .build();
                profileService.createProfile(dto);
            }
            redirectAttributes.addFlashAttribute("success", count + " profiles created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error in batch generation: " + e.getMessage());
        }
        return "redirect:/profiles";
    }
}