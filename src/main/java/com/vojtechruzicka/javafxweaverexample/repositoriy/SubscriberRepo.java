package com.vojtechruzicka.javafxweaverexample.repositoriy;

import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.model.Storage;
import com.vojtechruzicka.javafxweaverexample.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepo extends JpaRepository<Subscriber, Long> {

    @Query(value = "SELECT * FROM subscriber WHERE "
            + "LOWER(CAST(:#{#field} AS text)) LIKE LOWER(CONCAT('%', CAST(:#{#value} AS text), '%'))",
            nativeQuery = true)
    List<Subscriber> findByField(@Param("field") String field, @Param("value") String value);

    Optional<Subscriber> findSubscriberByTicketNumber(int ticketNumber);

    void deleteSubscriberByTicketNumber(int ticketNumber);
}
