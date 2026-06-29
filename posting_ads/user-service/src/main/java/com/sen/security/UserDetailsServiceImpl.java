package com.sen.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sen.entity.User;
import com.sen.repository.UserRepository;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPasswordHash(),
                !user.getBlocked(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}