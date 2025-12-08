package com.library.manager.driving.web.controllers;

import com.library.manager.application.ports.driving.BookServicePort;
import com.library.manager.domain.Book;
import com.library.manager.domain.BookGenre;
import com.library.manager.domain.valueobjects.BookFilter;
import com.library.manager.domain.valueobjects.PaginatedResult;
import com.library.manager.domain.valueobjects.PaginationQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ui/books")
@RequiredArgsConstructor
public class BookWebController {

    private final BookServicePort bookServicePort;

    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BookGenre genre,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model
    ) {
        // Crear filtro y query
        BookFilter filter = new BookFilter(title, author, genre, active);
        PaginationQuery query = new PaginationQuery(page, size, sortBy, sortDir);

        PaginatedResult<Book> result = bookServicePort.getAllWithFilters(filter, query);

        // Pasar datos a la vista
        model.addAttribute("page", result); // 'result' tiene .content(), .totalPages(), etc.
        model.addAttribute("filter", filter);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("genres", BookGenre.values()); // Para el select de filtro

        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("genres", BookGenre.values());
        return "books/create";
    }

    @PostMapping
    public String save(@ModelAttribute Book book) {
        bookServicePort.save(book);
        return "redirect:/ui/books?successMessage=Book created successfully";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookServicePort.findActiveById(id);
        model.addAttribute("book", book);
        return "books/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookServicePort.findActiveById(id);
        model.addAttribute("book", book);
        model.addAttribute("genres", BookGenre.values());
        return "books/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Book book) {
        book.setId(id); // Asegurar ID
        bookServicePort.update(book);
        return "redirect:/ui/books?successMessage=Book updated successfully";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookServicePort.deactivate(id);
        return "redirect:/ui/books?successMessage=Book deactivated successfully";
    }
}