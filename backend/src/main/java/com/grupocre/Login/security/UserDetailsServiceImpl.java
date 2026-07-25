package com.grupocre.Login.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.grupocre.Login.entity.User;
import com.grupocre.Login.entity.enums.UserStatus;
import com.grupocre.Login.repository.UserRepository;

import java.util.Collections;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrUsername(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return buildUserDetails(user);
    }

    public UserDetails loadUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        Set<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getNombre())
        );
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPasswordHash(),
                user.getEstado() == UserStatus.ACTIVO,
                true,
                true,
                user.getEstado() != UserStatus.BLOQUEADO,
                authorities
        );
    }
}
