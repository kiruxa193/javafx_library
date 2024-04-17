package com.vojtechruzicka.javafxweaverexample.repositoriy;

import com.vojtechruzicka.javafxweaverexample.model.BookLoan;
import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepo extends JpaRepository<Library, Long> {
    @Query(value = "SELECT * FROM library WHERE "
            + "LOWER(CAST(:#{#field} AS text)) LIKE LOWER(CONCAT('%', CAST(:#{#value} AS text), '%'))",
            nativeQuery = true)
    List<Library> findByField(@Param("field") String field, @Param("value") String value);

    Optional<Library> findLibraryByLibraryNumber(int libraryNumber);

    void deleteLibraryByLibraryNumber(int libraryNumber);
}
