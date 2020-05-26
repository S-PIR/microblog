package com.example.microblog.service.user;

import com.example.microblog.domain.Role;
import com.example.microblog.domain.User;
import com.example.microblog.repos.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MailSender mailSender;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    void addUser() {
        User user =new User();
        user.setEmail("some@mail.ru");
        boolean isUserCreated = userService.addUser(user);
        Assert.assertTrue(isUserCreated);
        Assert.assertNotNull(user.getActivationCode());
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.ROLE_USER)));

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    public void addUserFailTest(){
        User user = new User();
        user.setUsername("John");
        Mockito.doReturn(new User())
                .when(userRepo)
                .findByUsername("John");
        boolean isUserCreated = userService.addUser(user);
        Assert.assertFalse(isUserCreated);
        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    void activateUser() {
        User user = new User();
        user.setActivationCode("bingo!");
        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("activate");
        boolean isUserActivated = userService.activateUser("activate");

        Assert.assertTrue(isUserActivated);
        Assert.assertNull(user.getActivationCode());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFailTest() {
        boolean isActivatedUser = userService.activateUser("activate me");
        Assert.assertFalse(isActivatedUser);
        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }


}