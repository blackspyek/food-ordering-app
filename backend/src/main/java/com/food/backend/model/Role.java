package com.food.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User role")
public enum Role {
    @Schema(description = "Regular user role")
    ROLE_USER,
    @Schema(description = "Manager role with elevated permissions")
    ROLE_MANAGER,
    @Schema(description = "Employee role with limited permissions")
    ROLE_EMPLOYEE,
}