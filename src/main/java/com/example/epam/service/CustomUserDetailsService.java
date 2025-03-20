package com.example.epam.service;

import com.example.epam.dao.UserDao;
import com.example.epam.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;
    private final SessionFactory sessionFactory;

    public CustomUserDetailsService(UserDao userDao, SessionFactory sessionFactory) {
        this.userDao = userDao;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try (var session = sessionFactory.openSession()) {
            User user = userDao.findByUsername(username, session)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getIsActive() ? "USER" : "INACTIVE")
                    .build();
        }
    }
}
