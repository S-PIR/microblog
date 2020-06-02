package com.example.microblog.service.user.message;

import com.example.microblog.domain.Message;
import com.example.microblog.domain.User;
import com.example.microblog.domain.dto.MessageDto;
import com.example.microblog.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
public class MessageService {
    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    EntityManager entityManager;

    public Page<MessageDto> messageList(String filter, Pageable pageable, User currentUser) {
        if (filter != null && !filter.isEmpty()) {
            return messageRepo.findByTag(filter, pageable, currentUser);
        } else {
            return messageRepo.findAll(pageable, currentUser);
        }
    }

    public Page<MessageDto> messageListForUser(User author, Pageable pageable, User currentUser) {
        return messageRepo.findByUser(author, pageable, currentUser);
    }


}
