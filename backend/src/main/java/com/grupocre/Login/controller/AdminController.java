package com.grupocre.Login.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.grupocre.Login.entity.User;
import com.grupocre.Login.service.AdminService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PutMapping("/users/{id}/unlock")
    public ResponseEntity<Map<String, String>> unlockUser(@PathVariable Integer id,
                                                          HttpServletRequest request) {
        adminService.unlockUser(id, request.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Usuario desbloqueado con éxito"));
    }

    @GetMapping("/users/blocked")
    public ResponseEntity<List<User>> getBlockedUsers() {
        return ResponseEntity.ok(adminService.getBlockedUsers());
    }
}