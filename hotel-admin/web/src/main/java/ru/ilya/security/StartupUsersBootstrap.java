package ru.ilya.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ru.ilya.dao.jpa.AppUserDaoJpa;
import ru.ilya.model.AppUser;
import ru.ilya.model.UserRole;

@Component
public class StartupUsersBootstrap implements SmartInitializingSingleton {
    private static final Logger logger = LoggerFactory.getLogger(StartupUsersBootstrap.class);

    private final AppUserDaoJpa appUserDaoJpa;
    private final PasswordEncoder passwordEncoder;

    public StartupUsersBootstrap(AppUserDaoJpa appUserDaoJpa, PasswordEncoder passwordEncoder) {
        this.appUserDaoJpa = appUserDaoJpa;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterSingletonsInstantiated() {
        ensureUser("admin", "admin", UserRole.ADMIN);
        ensureUser("user", "user", UserRole.USER);
    }

    private void ensureUser(String login, String rawPassword, UserRole role) {
        AppUser existing = appUserDaoJpa.findByLogin(login);
        if (existing != null) {
            return;
        }

        AppUser user = new AppUser(login, passwordEncoder.encode(rawPassword), role);
        appUserDaoJpa.create(user);
        logger.info("Создан стартовый пользователь login={}, role={}", login, role);
    }
}