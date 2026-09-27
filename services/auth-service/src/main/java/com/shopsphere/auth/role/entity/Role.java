package com.shopsphere.auth.role.entity;

import com.shopsphere.auth.role.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleName name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}