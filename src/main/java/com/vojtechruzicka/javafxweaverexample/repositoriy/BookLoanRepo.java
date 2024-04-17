package com.vojtechruzicka.javafxweaverexample.repositoriy;


import com.vojtechruzicka.javafxweaverexample.model.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLoanRepo extends JpaRepository<BookLoan, Long> {
    @Query(value = "SELECT * FROM book_loan WHERE "
            + "LOWER(CAST(:#{#field} AS text)) LIKE LOWER(CONCAT('%', CAST(:#{#value} AS text), '%'))",
            nativeQuery = true)
    List<BookLoan> findByField(@Param("field") String field, @Param("value") String value);

    Optional<BookLoan> findByBookLoanNumber(int bookLoan);
    void deleteBookLoanByBookLoanNumber(int bookLoanNumber);
}
