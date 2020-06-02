package com.example.microblog.domain;

import com.example.microblog.domain.util.MessageHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Please fill the message")
    @Length(max = 2048, message = "Message too long (more then 2kB)")
    private String text;

    @Length(max = 255, message = "Message too long (more then 255)")
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    private String filename;

    @Column(name = "timestamp")
    private LocalDateTime date;

    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = { @JoinColumn(name = "message_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> likes = new HashSet<>();

    public Message(String text, String tag, User user) {
        this.text = text;
        this.tag = tag;
        this.author = user;
        this.date = LocalDateTime.now();
    }

    public String getAuthorName() {
        return MessageHelper.getAuthorName(author);
    }

    public String printDate() {
        return MessageHelper.printDate(date);
    }

}
