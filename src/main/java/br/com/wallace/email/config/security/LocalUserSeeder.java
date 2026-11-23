package br.com.wallace.email.config.security;

import br.com.wallace.email.enums.RoleName;
import br.com.wallace.email.models.UserModel;
import br.com.wallace.email.repositories.RoleRepository;
import br.com.wallace.email.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalUserSeeder implements ApplicationRunner {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    @Value("${app.seed-users.admin-password}")
    private String adminPassword;
    @Value("${app.seed-users.user-password}")
    private String userPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var adm = roles.findByRoleName(RoleName.ROLE_ADM).orElseThrow();
        var usr = roles.findByRoleName(RoleName.ROLE_USER).orElseThrow();
        create("admin", adminPassword, List.of(adm, usr));
        create("user", userPassword, List.of(usr));
    }

    private void create(String username, String rawPassword, List<br.com.wallace.email.models.RoleModel> userRoles) {
        if (!users.existsByUsername(username)) {
            users.save(new UserModel(username, encoder.encode(rawPassword), userRoles));
            log.info("Usuário local '{}' criado", username);
        }
    }
}
