package com.library.entity;

import java.time.LocalDate;
import java.util.Comparator;

public class Book implements Comparable<Book> {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publicationDate; 
    private int quantity;

    public Book() {}

    public Book(String id, String title, String author, String isbn, LocalDate publicationDate, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public int compareTo(Book other) {
        return this.id.compareTo(other.id);
    }

    public static Comparator<Book> ByAuthor = Comparator.comparing(Book::getAuthor);
    
    @Override
    public String toString() {
        return String.join("|", 
            id, title, author, isbn, 
            publicationDate.toString(), String.valueOf(quantity));
    }
}