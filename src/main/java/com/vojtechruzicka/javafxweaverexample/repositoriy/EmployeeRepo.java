package com.vojtechruzicka.javafxweaverexample.repositoriy;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    @Query(value = "SELECT * FROM employee WHERE "
            + "LOWER(CAST(:#{#field} AS text)) LIKE LOWER(CONCAT('%', CAST(:#{#value} AS text), '%'))",
            nativeQuery = true)
    List<Employee> findByField(@Param("field") String field, @Param("value") String value);


    Optional<Employee> findEmployeeByEmployeeId(int employeeId);

    void deleteEmployeeByEmployeeId(int bookNumber);
}
