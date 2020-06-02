package com.example.microblog.domain.dto;

import com.example.microblog.domain.Message;
import com.example.microblog.domain.User;
import com.example.microblog.domain.util.MessageHelper;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String filename;
    private LocalDateTime date;
    private Long likes;
    private boolean meLiked;

    public MessageDto(Message message, Long likes, boolean meLiked) {
        this.id = message.getId();
        this.text = message.getText();
        this.tag = message.getTag();
        this.author = message.getAuthor();
        this.filename = message.getFilename();
        this.date = message.getDate();
        this.likes = likes;
        this.meLiked = meLiked;
    }

    public String getAuthorName() {
        return MessageHelper.getAuthorName(author);
    }

    public String printDate() {
        return MessageHelper.printDate(date);
    }


}
