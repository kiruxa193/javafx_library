package com.vojtechruzicka.javafxweaverexample.repositoriy;

import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepo extends JpaRepository<Storage, Long> {

    @Query(value = "SELECT * FROM storage WHERE "
            + "LOWER(CAST(:#{#field} AS text)) LIKE LOWER(CONCAT('%', CAST(:#{#value} AS text), '%'))",
            nativeQuery = true)
    List<Storage> findByField(@Param("field") String field, @Param("value") String value);

    Optional<Storage> findStorageByStorageNumber(int storageNumber);

    void deleteStorageByStorageNumber(int storageNumber);
}

