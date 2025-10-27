package com.food.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "User entity")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier for the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "email", example = "johndoe")
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    @Schema(description = "Password", example = "password")
    private String password;

    @Column(nullable = false)
    @Schema(description = "phone_number", example = "+48515553432")
    private String phoneNumber;

    @Column(nullable = false)
    @Schema(description = "name", example = "John")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Indicates if the user is enabled", example = "true")
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER) //
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Schema(description = "User's roles")
    private Set<Role> roles;

    @Column
    @JsonIgnore
    @Schema(description = "Password reset token")
    private String passwordResetToken;

    @Column
    @JsonIgnore
    @Schema(description = "Password reset token expiry time")
    private LocalDateTime passwordResetTokenExpiry;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role::name)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


}
