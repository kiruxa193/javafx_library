package com.vojtechruzicka.javafxweaverexample.repositoriy;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM book WHERE "
            + "LOWER(CAST(:#{#field} AS text)) LIKE LOWER(CONCAT('%', CAST(:#{#value} AS text), '%'))",
            nativeQuery = true)
    List<Book> findByField(@Param("field") String field, @Param("value") String value);


    Optional<Book> findByBookNumber(int bookNumber);


    void deleteBookByBookNumber(int bookNumber);
}



