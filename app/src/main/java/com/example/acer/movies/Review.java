package com.example.acer.movies;

import java.io.Serializable;

/**
 * Created by KeshavAggarwal on 14/03/17.
 */

public class Review implements Serializable {

    String author;
    String content;
    String url;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}