package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e,
                                              RedirectAttributes redirectAttributes,
                                              HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", "Photo size exceeds the maximum allowed limit (10MB). Please choose a smaller file.");
        
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/edit/")) {
            return "redirect:" + referer;
        }
        return "redirect:/profiles/create";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e,
                                          RedirectAttributes redirectAttributes,
                                          HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
        
        String referer = request.getHeader("Referer");
        if (referer != null) {
            return "redirect:" + referer;
        }
        return "redirect:/profiles";
    }
}