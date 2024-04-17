package com.vojtechruzicka.javafxweaverexample.service;

import com.vojtechruzicka.javafxweaverexample.model.Storage;
import com.vojtechruzicka.javafxweaverexample.model.Subscriber;
import com.vojtechruzicka.javafxweaverexample.repositoriy.SubscriberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {

    private final SubscriberRepo subscriberRepo;

    @Autowired
    public SubscriberService(SubscriberRepo subscriberRepo) {
        this.subscriberRepo = subscriberRepo;
    }

    @Transactional
    public void addSubscriber(Subscriber sub) {
        if (isSubscriberAlreadyExists(sub)) {
            throw new IllegalArgumentException("Subscriber with the same values already exists.");

        }
        subscriberRepo.save(sub);
    }

    @Transactional
    public void updateSubscriber(Subscriber sub) {
        // Проверяем, что книга существует
//        if (!bookRepo.existsById((long) book.getBookNumber())) {
//            // Можно бросить исключение или выполнить другие действия в случае ошибки
//            throw new IllegalArgumentException("Book does not exist.");
//        }
        subscriberRepo.save(sub);
    }

    @Transactional
    public List<Subscriber> getAllSubscribers() {
        return subscriberRepo.findAll();
    }


    @Transactional
    public void deleteSubscriber(int subscriberNumber) {
        subscriberRepo.deleteSubscriberByTicketNumber(subscriberNumber);
    }




    private boolean isSubscriberAlreadyExists(Subscriber sub) {
        List<Subscriber> existingSubscribers = subscriberRepo.findAll();

        if(existingSubscribers.size() != 1) {
            for (Subscriber existingSubscriber : existingSubscribers) {
                if (areSubscribersEqual(existingSubscriber, sub)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean areSubscribersEqual(Subscriber sub1, Subscriber sub2) {
        // Сравнивайте каждое поле ваших объектов Book
        return sub1.getTicketNumber() == (sub2.getTicketNumber())
                && sub1.getLastName().equals(sub2.getLastName())
                && sub1.getFirstName().equals(sub2.getFirstName())
                && sub1.getMiddleName().equals(sub2.getMiddleName());
    }

    public List<Integer> getAllSubscriberNumbers() {
        return subscriberRepo.findAll().stream()
                .map(Subscriber::getTicketNumber)
                .collect(Collectors.toList());
    }

    public Optional<Subscriber> getSubscribersByTicketNumbers(int ticketNumber) {
        return subscriberRepo.findSubscriberByTicketNumber(ticketNumber);
    }
}
