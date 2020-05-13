package com.example.microblog.repos;

import com.example.microblog.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface MessageRepo extends CrudRepository<Message, Long> {
    List<Message> findByTag(String tag);
}
