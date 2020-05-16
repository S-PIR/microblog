package com.example.microblog.repos;

import com.example.microblog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long>{
    User findByUsername(String username);
}
