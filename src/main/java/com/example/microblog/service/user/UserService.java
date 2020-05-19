package com.example.microblog.service.user;

import com.example.microblog.domain.Role;
import com.example.microblog.domain.User;
import com.example.microblog.repos.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Value("${email.activation.link}")
    private String activationUri;

    private final UserRepo userRepo;
    private final MailSender mailSender;

    public UserService(UserRepo userRepo, MailSender mailSender) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user) {
        User userFomDb = userRepo.findByUsername(user.getUsername());
        if (userFomDb != null){
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);
        String message = String.format(
                "Hello, %s!\n" +
                "Welcome to Microblog.\nPlease, visit next link:\n" + activationUri,
                user.getUsername(),
                user.getActivationCode()
                );
        if (!StringUtils.isEmpty(user.getEmail())) {
            mailSender.send(user.getEmail(), "Activation code", message);
        }
        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        userRepo.save(user);
        return true;
        }

}