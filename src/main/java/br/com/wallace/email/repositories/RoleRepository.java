package br.com.wallace.email.repositories;

import br.com.wallace.email.enums.RoleName;
import br.com.wallace.email.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {
    Optional<RoleModel> findByRoleName(RoleName roleName);
}
