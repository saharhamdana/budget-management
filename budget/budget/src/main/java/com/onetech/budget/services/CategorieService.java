package com.onetech.budget.services;

import com.onetech.budget.models.Categorie;
import com.onetech.budget.repositories.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service

public class CategorieService {
    private final CategorieRepository repository;

    public CategorieService(CategorieRepository repository) {
        this.repository = repository;
    }

    public List<Categorie> getAll() {
        return repository.findAll();
    }

    public Optional<Categorie> getById(Long id) {
        return repository.findById(id);
    }

    public Categorie create(Categorie categorie) {
        return repository.save(categorie);
    }

    public Categorie update(Long id, Categorie updatedCategorie) {
        return repository.findById(id).map(c -> {
            c.setNom(updatedCategorie.getNom());
            return repository.save(c);
        }).orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
