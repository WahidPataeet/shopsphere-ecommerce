package com.shopsphere.auth.role.repository;

import com.shopsphere.auth.role.RoleName;
import com.shopsphere.auth.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}