package com.example.microblog.domain.util;

import com.example.microblog.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public abstract class MessageHelper {
    public static String getAuthorName(User author) {
        return author != null? author.getUsername(): "<none>";
    }

    public static String printDate(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.UK)) : "<none>";
    }

}
