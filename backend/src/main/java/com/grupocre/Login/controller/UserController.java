package com.grupocre.Login.controller;


import com.grupocre.Login.dto.UserProfile;
import com.grupocre.Login.security.CustomUserDetails;
import com.grupocre.Login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public UserProfile getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getProfile(userDetails.getUsername());
    }
}