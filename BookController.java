package com.library.controller;

import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class BookController {
    
    private final BookService bookService = new BookService();
    private final int PAGE_SIZE = 5;

    @GetMapping
    public String listBooks(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "id") String sort,
        Model model) {
        
        List<Book> filteredBooks = bookService.search(search);
        List<Book> sortedBooks = bookService.sort(filteredBooks, sort);
        List<Book> pagedBooks = bookService.getPage(sortedBooks, page, PAGE_SIZE);
        
        int totalItems = filteredBooks.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        model.addAttribute("books", pagedBooks);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("hasBooks", !sortedBooks.isEmpty());
        
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable(required = false) String id, Model model) {
        Book book;
        if (id.equals("new")) {
            book = new Book();
        } else {
            book = bookService.getBookById(id);
            if (book == null) {
                return "redirect:/"; 
            }
        }
        model.addAttribute("book", book);
        return "edit-book";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.save(book);
            redirectAttributes.addFlashAttribute("message", "Book saved successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error saving book: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Book deleted successfully!");
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/";
    }
}
