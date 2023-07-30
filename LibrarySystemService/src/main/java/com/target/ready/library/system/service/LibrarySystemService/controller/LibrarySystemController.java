package com.target.ready.library.system.service.LibrarySystemService.controller;

import com.target.ready.library.system.service.LibrarySystemService.entity.Book;
import com.target.ready.library.system.service.LibrarySystemService.entity.BookCategory;
import com.target.ready.library.system.service.LibrarySystemService.exceptions.ResourceNotFoundException;
import com.target.ready.library.system.service.LibrarySystemService.repository.BookCategoryRepository;
import com.target.ready.library.system.service.LibrarySystemService.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("library/v1/")
public class LibrarySystemController {

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final BookCategoryRepository bookCategoryRepository;

    public LibrarySystemController(BookRepository bookRepository, BookCategoryRepository bookCategoryRepository) {
        this.bookRepository = bookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    @GetMapping("books_directory/{pageNumber}/{pageSize}")
    public ResponseEntity<List<Book>> getAllBooks(@PathVariable int pageNumber, @PathVariable int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<Book> findBooks = bookRepository.findAll(pageable);
        List<Book> books =  findBooks.toList();
        return new ResponseEntity<>(books,HttpStatus.OK);
    }

    @PostMapping("inventory/books")

    public Book addBook(@RequestBody Book book){return bookRepository.save(book);}



    @DeleteMapping("book/{bookId}")
    public String deleteBook(@PathVariable int bookId) {
        bookRepository.deleteById(bookId);
        return "Book Deleted Successfully";
    }

    @GetMapping("book/{bookId}")
    public Book findByBookId(@PathVariable int bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }


    @GetMapping("book/category/{categoryName}")
    public List<Book> findBookByCategoryName(@PathVariable String categoryName){
        List<BookCategory> bookCategory;
        List<Book> bookDetails = new ArrayList<>();
        bookCategory = bookCategoryRepository.findByCategoryName(categoryName);
        for(BookCategory bookCategory1 : bookCategory){
            int b1 = bookCategory1.getBookId();
            Book book = findByBookId(b1);
            bookDetails.add(book);
        }
        return bookDetails;
    }

    @GetMapping("books/{bookName}")
    public ResponseEntity<List<Book>> findByBookName(@PathVariable String bookName) {
        List<Book> books= bookRepository.findByBookName(bookName);

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PutMapping("inventory/book/update/{id}")
    public Book updateBookDetails(@PathVariable("id") int id, @RequestBody Book book ){
        Book previousBook = bookRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Book with bookID: "+ id + " not found in database"));
        previousBook.setBookName(book.getBookName());
        previousBook.setBookDescription(book.getBookDescription());
        previousBook.setAuthorName(book.getAuthorName());
        previousBook.setPublicationYear(book.getPublicationYear());
        return bookRepository.save(previousBook);
    }

}