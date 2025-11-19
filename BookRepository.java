package com.library.repository;

import com.library.entity.Book;
import com.library.factory.BookFactory;
import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.time.format.DateTimeParseException;

public class BookRepository {
    private static BookRepository instance;
    private final List<Book> books = new CopyOnWriteArrayList<>();
    private static final String DATA_FILE = "library_data.txt";
    private static final Pattern ID_PATTERN = Pattern.compile("LIB-\\d{3}");
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{3}-\\d-\\d{2}-\\d{6}-\\d|\\d{13})$");

    private BookRepository() {
        loadDataAsync(); 
    }

    public static BookRepository getInstance() {
        if (instance == null) {
            synchronized (BookRepository.class) {
                if (instance == null) {
                    instance = new BookRepository();
                }
            }
        }
        return instance;
    }

    private void loadDataAsync() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(Pattern.quote("|"));
                    if (data.length == 6) {
                        try {
                            Book book = BookFactory.createBook(data);
                            books.add(book);
                        } catch (DateTimeParseException | NumberFormatException e) {
                            System.err.println("Skipping corrupted record: " + line);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                // Ignore, file will be created on first save
            } catch (IOException e) {
                System.err.println("Error reading data file: " + e.getMessage());
            }
        }).start();
    }
    
    public synchronized void saveAll() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            StringBuilder sb = new StringBuilder();
            for (Book book : books) {
                sb.append(book.toString()).append("\n");
            }
            writer.write(sb.toString());
        } catch (IOException e) {
            System.err.println("Error saving data file: " + e.getMessage());
        }
    }

    public List<Book> findAll() { return books; }
    
    public void add(Book book) throws IllegalArgumentException {
        if (!ID_PATTERN.matcher(book.getId()).matches() || !ISBN_PATTERN.matcher(book.getIsbn()).matches()) {
            throw new IllegalArgumentException("Invalid ID or ISBN format.");
        }
        if (books.stream().anyMatch(b -> b.getId().equals(book.getId()))) {
            throw new IllegalArgumentException("Book ID already exists.");
        }
        books.add(book);
        saveAll();
    }
    
    public void deleteById(String id) {
        books.removeIf(b -> b.getId().equals(id));
        saveAll();
    }
    
    public Book findById(String id) {
        return books.stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public void update(Book updatedBook) throws IllegalArgumentException {
        Book existingBook = findById(updatedBook.getId());
        if (existingBook != null) {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setPublicationDate(updatedBook.getPublicationDate());
            existingBook.setQuantity(updatedBook.getQuantity());
            saveAll();
        } else {
            throw new IllegalArgumentException("Book not found for update.");
        }
    }
}
