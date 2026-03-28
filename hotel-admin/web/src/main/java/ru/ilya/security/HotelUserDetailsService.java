package ru.ilya.security;
//Реализация UserDetailsService. Загружает пользователя из базы данных, 
// чтобы Spring Security понимал с чем работает
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.ilya.dao.jpa.AppUserDaoJpa;
import ru.ilya.model.AppUser;

@Service
public class HotelUserDetailsService implements UserDetailsService{
    private final AppUserDaoJpa appUserDaoJpa;

    public HotelUserDetailsService(AppUserDaoJpa appUserDaoJpa){
        this.appUserDaoJpa=appUserDaoJpa;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserDaoJpa.findByLogin(username);
        if (user==null){
            throw new UnsupportedOperationException("Пользователь с login=" + username + " не найден");
        }
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return new User(user.getLogin(), user.getPassword(), authorities);
    }
}
