package com.example.kpi.controllers;

import com.example.kpi.dto.UserResponse;
import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import com.example.kpi.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CEO')")
public class RoleController {
    private final UserService userService;

    @PutMapping("/add-role/{userId}")
    public ResponseEntity<UserResponse> addRoleToUser(@PathVariable Long userId, @RequestParam Role role) {
        var updatedUser = userService.addRoleToUser(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удаление роли у пользователя.
     *
     * @param userId ID пользователя.
     * @param role   Роль для удаления.
     * @return Обновлённый пользователь.
     */
    @PutMapping("/remove-role/{userId}")
    public ResponseEntity<UserResponse> removeRoleFromUser(@PathVariable Long userId, @RequestParam Role role) {
        var updatedUser = userService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok(updatedUser);
    }
}
