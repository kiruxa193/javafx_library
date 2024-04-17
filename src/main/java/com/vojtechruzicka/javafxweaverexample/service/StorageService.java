package com.vojtechruzicka.javafxweaverexample.service;

import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.model.Storage;
import com.vojtechruzicka.javafxweaverexample.repositoriy.StorageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final StorageRepo storageRepo;

    @Autowired
    public StorageService(StorageRepo storageRepo) {
        this.storageRepo = storageRepo;
    }

    @Transactional
    public void addStorage(Storage storage) {
        if (isStorageAlreadyExists(storage)) {
            throw new IllegalArgumentException("Storage with the same values already exists.");

        }
        storageRepo.save(storage);
    }

    @Transactional
    public void updateStorage(Storage storage) {
        // Проверяем, что книга существует
//        if (!bookRepo.existsById((long) book.getBookNumber())) {
//            // Можно бросить исключение или выполнить другие действия в случае ошибки
//            throw new IllegalArgumentException("Book does not exist.");
//        }
        storageRepo.save(storage);
    }

    @Transactional
    public List<Storage> getAllStorages() {
        return storageRepo.findAll();
    }


    @Transactional
    public void deleteStorage(int storageNumber) {
        storageRepo.deleteStorageByStorageNumber(storageNumber);
    }




    private boolean isStorageAlreadyExists(Storage storage) {
        List<Storage> existingStorages = storageRepo.findAll();

        if(existingStorages.size() != 1) {
            for (Storage existingStorage : existingStorages) {
                if (areStoragesEqual(existingStorage, storage)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean areStoragesEqual(Storage stor1, Storage stor2) {
        // Сравнивайте каждое поле ваших объектов Book
        return stor1.getStorageNumber() == (stor2.getStorageNumber())
                && stor1.getLibraryNumber() == (stor2.getLibraryNumber())
                && stor1.getFloor() == (stor2.getFloor())
                && stor1.getCapacity() == (stor2.getCapacity());
    }

    public List<Integer> getAllStorageNumbers() {
        return storageRepo.findAll().stream()
                .map(Storage::getStorageNumber)
                .collect(Collectors.toList());
    }

    public Optional<Storage> getStoragesByStorageNumbers(int storageNumber) {
        return storageRepo.findStorageByStorageNumber(storageNumber);
    }
}
