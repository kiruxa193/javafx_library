package com.vojtechruzicka.javafxweaverexample.service;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import com.vojtechruzicka.javafxweaverexample.model.BookLoan;
import com.vojtechruzicka.javafxweaverexample.repositoriy.BookLoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookLoanService  {

    private final BookLoanRepo bookLoanRepo;

    @Autowired
    public BookLoanService(BookLoanRepo bookLoanRepo) {
        this.bookLoanRepo = bookLoanRepo;
    }

    @Transactional
    public void addBookLoan(BookLoan bookLoan) {
        if (isBookLoanAlreadyExists(bookLoan)) {
            throw new IllegalArgumentException("BookLoan with the same values already exists.");
        }
        bookLoanRepo.save(bookLoan);
    }

    @Transactional
    public void updateBookLoan(BookLoan bookLoan) {
//        // Проверяем, что книга существует
//        if (!bookLoanRepo.existsById((long) bookLoan.getBookLoanNumber())) {
//            // Можно бросить исключение или выполнить другие действия в случае ошибки
//            throw new IllegalArgumentException("Book does not exist.");
//        }
        bookLoanRepo.save(bookLoan);
    }

    @Transactional
    public List<BookLoan> getAllBookLoans() {
        return bookLoanRepo.findAll();
    }


    @Transactional
    public void deleteBookLoan(int bookLoanNumber) {
        bookLoanRepo.deleteBookLoanByBookLoanNumber(bookLoanNumber);
    }

    public List<Integer> getAllBookLoanNumbers() {
        return bookLoanRepo.findAll().stream()
                .map(BookLoan::getBookLoanNumber)
                .collect(Collectors.toList());
    }

    private boolean isBookLoanAlreadyExists(BookLoan newBookLoan) {
        List<BookLoan> existingBookLoans = bookLoanRepo.findAll();

        for (BookLoan existingBookLoan : existingBookLoans) {
            if (areBookLoansEqual(existingBookLoan, newBookLoan)) {
                return true;
            }
        }

        return false;
    }

    private boolean areBookLoansEqual(BookLoan loan1, BookLoan loan2) {
        // Сравнивайте каждое поле ваших объектов BookLoan
        return loan1.getTicketNumber() == loan2.getTicketNumber()
                && loan1.getBookNumber() == loan2.getBookNumber()
                && loan1.getLoanDate().equals(loan2.getLoanDate())
                && Objects.equals(loan1.getReturnDate(), loan2.getReturnDate());
    }

    public Optional<BookLoan> getBookLoanByBookLoanNumber(int bookLoanNumber) {
        return bookLoanRepo.findByBookLoanNumber(bookLoanNumber);
    }

}
