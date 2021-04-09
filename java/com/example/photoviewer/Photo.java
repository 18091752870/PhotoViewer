package com.example.photoviewer;

import android.graphics.Bitmap;

public class Photo {
    private String id,title,author;
    private Bitmap photo;

    Photo(String id_, String title_, String author_) {
        id=id_;
        title=title_;
        author=author_;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }
}
