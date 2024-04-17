package com.vojtechruzicka.javafxweaverexample.service;

import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.repositoriy.LibraryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryRepo libraryRepo;

    @Autowired
    public LibraryService(LibraryRepo libraryRepo) {
        this.libraryRepo = libraryRepo;
    }

    @Transactional
    public void addLibrary(Library library) {
        if (isLibraryAlreadyExists(library)) {
            throw new IllegalArgumentException("Library with the same values already exists.");

        }
        libraryRepo.save(library);
    }

    @Transactional
    public void updateLibrary(Library library) {
        // Проверяем, что книга существует
//        if (!bookRepo.existsById((long) book.getBookNumber())) {
//            // Можно бросить исключение или выполнить другие действия в случае ошибки
//            throw new IllegalArgumentException("Book does not exist.");
//        }
        libraryRepo.save(library);
    }

    @Transactional
    public List<Library> getAllLibraries() {
        return libraryRepo.findAll();
    }


    @Transactional
    public void deleteLibrary(int libraryNumber) {
        libraryRepo.deleteLibraryByLibraryNumber(libraryNumber);
    }




    private boolean isLibraryAlreadyExists(Library library) {
        List<Library> existingLibraries = libraryRepo.findAll();

        if(existingLibraries.size() != 1) {
            for (Library existingLibrary : existingLibraries) {
                if (areLibrariesEqual(existingLibrary, library)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean areLibrariesEqual(Library lib1, Library lib2) {
        // Сравнивайте каждое поле ваших объектов Book
        return lib1.getLibraryNumber() == (lib2.getLibraryNumber())
                && lib1.getLibraryName().equals(lib2.getLibraryName())
                && lib1.getAddress().equals(lib2.getAddress())
                && lib1.getBookCount() == (lib2.getBookCount());
    }

    public List<Integer> getAllLibraryNumbers() {
        return libraryRepo.findAll().stream()
                .map(Library::getLibraryNumber)
                .collect(Collectors.toList());
    }

    public Optional<Library> getLibrariesByLibraryNumbers(int libraryNumber) {
        return libraryRepo.findLibraryByLibraryNumber(libraryNumber);
    }
}
