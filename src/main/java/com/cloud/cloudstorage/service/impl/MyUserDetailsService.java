package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.model.Role;
import com.cloud.cloudstorage.model.User;
import com.cloud.cloudstorage.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("No user found with username: " + username));

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, mapRolesToAuthorities(user.getRoles()));
    }

    private List<? extends GrantedAuthority> mapRolesToAuthorities (List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }
}
