package com.example.microblog.repos;

import com.example.microblog.domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepo extends CrudRepository<Message, Long> {
    
}
