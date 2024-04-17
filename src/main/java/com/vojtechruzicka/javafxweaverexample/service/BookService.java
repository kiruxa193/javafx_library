package com.vojtechruzicka.javafxweaverexample.service;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import com.vojtechruzicka.javafxweaverexample.repositoriy.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService{

    private final BookRepo bookRepo;

    @Autowired
    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Transactional
    public void addBook(Book book) {
        if (isBookAlreadyExists(book)) {
            throw new IllegalArgumentException("Book with the same values already exists.");
        }
        bookRepo.save(book);
    }

    @Transactional
    public void updateBook(Book book) {
        // Проверяем, что книга существует
//        if (!bookRepo.existsById((long) book.getBookNumber())) {
//            // Можно бросить исключение или выполнить другие действия в случае ошибки
//            throw new IllegalArgumentException("Book does not exist.");
//        }
        bookRepo.save(book);
    }

    @Transactional
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }


    @Transactional
    public void deleteBook(int bookNumber) {
        bookRepo.deleteBookByBookNumber(bookNumber);
    }




    private boolean isBookAlreadyExists(Book newBook) {
        List<Book> existingBooks = bookRepo.findAll();

        for (Book existingBook : existingBooks) {
            if (areBooksEqual(existingBook, newBook)) {
                return true;
            }
        }

        return false;
    }

    private boolean areBooksEqual(Book book1, Book book2) {
        // Сравнивайте каждое поле ваших объектов Book
        return book1.getBookCipher().equals(book2.getBookCipher())
                && book1.getAuthor().equals(book2.getAuthor())
                && book1.getTitle().equals(book2.getTitle())
                && book1.getPublisher().equals(book2.getPublisher())
                && book1.getPublicationYear() == book2.getPublicationYear()
                && Double.compare(book1.getPrice(), book2.getPrice()) == 0
                && book1.getAcquisitionDate().equals(book2.getAcquisitionDate())
                && book1.getStorageNumber() == book2.getStorageNumber();
    }

    public List<Integer> getAllBookNumbers() {
        return bookRepo.findAll().stream()
                .map(Book::getBookNumber)
                .collect(Collectors.toList());
    }

    public Optional<Book> getBookByNumber(int bookNumber) {
        return bookRepo.findByBookNumber(bookNumber);
    }

}
