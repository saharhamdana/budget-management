package com.onetech.budget.services;

import com.onetech.budget.DTO.TransactionRequestDTO;
import com.onetech.budget.models.Categorie;
import com.onetech.budget.models.Client;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.CategorieRepository;
import com.onetech.budget.repositories.ClientRepository;
import com.onetech.budget.repositories.TransactionRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final CategorieRepository categorieRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, ClientRepository clientRepository, CategorieRepository categorieRepository) {
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
        this.categorieRepository = categorieRepository;
    }

    public Transaction saveTransaction(TransactionRequestDTO dto) {
       /* Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id " + dto.getClientId()));
*/
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject(); // 'sub' contient généralement l'ID utilisateur

        Transaction transaction = new Transaction();
        transaction.setMontant(dto.getMontant());
        transaction.setDateOpertation(dto.getDateOpertation());
        transaction.setDateValeur(dto.getDateValeur());
        transaction.setClient(userId);

        //transaction.setClient(client);

        return transactionRepository.save(transaction);
    }

    public Transaction assignCategorieToTransaction(Long transactionId, Long categorieId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        transaction.setCategorie(categorie);
        return transactionRepository.save(transaction);
    }


    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction deleteById(Long id) {
        Transaction transaction = findById(id);
        transactionRepository.deleteById(id);
        return transaction;
    }

    public List<Transaction> findByClientConnected() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();
        return transactionRepository.findByClient(userId);
    }

    /*
    public List<Transaction> processExcelFile(MultipartFile file) throws IOException {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();
        List<Transaction> savedTransactions = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                Transaction transaction = new Transaction();
                transaction.setClient(userId);

                // Lecture des cellules selon le format BIAT
                transaction.setDateOpertation(parseBiatDate(getCellStringValue(row.getCell(0)))); // Date opération
                transaction.setDescription(getCellStringValue(row.getCell(1))); // Libellé
                transaction.setReference(getCellStringValue(row.getCell(2))); // Référence
                transaction.setDateValeur(parseBiatDate(getCellStringValue(row.getCell(3)))); // Date valeur

                // Gestion montant (débit/crédit)
                Double debit = getCellNumericValue(row.getCell(4));
                Double credit = getCellNumericValue(row.getCell(5));

                if (debit != null) {
                    transaction.setMontant(-debit); // Débit = montant négatif
                } else if (credit != null) {
                    transaction.setMontant(credit); // Crédit = montant positif
                } else {
                    continue; // Skip si pas de montant
                }

                savedTransactions.add(transactionRepository.save(transaction));
            }
        }
        return savedTransactions;
    }*/


    public List<Transaction> processExcelFile(MultipartFile file) throws IOException {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        List<Transaction> newlyAddedTransactions = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String reference = getCellStringValue(row.getCell(2)); // Référence

                if (transactionRepository.existsByReferenceAndClient(reference, userId)) {
                    continue; // ⛔ Déjà existante => skip
                }

                Double debit = getCellNumericValue(row.getCell(4));
                if (debit == null) {
                    continue; // ⛔ Ignore les lignes sans débit
                }

                Transaction transaction = new Transaction();
                transaction.setClient(userId);

                // Lecture des données
                transaction.setDateOpertation(parseBiatDate(getCellStringValue(row.getCell(0))));
                transaction.setDescription(getCellStringValue(row.getCell(1)));
                transaction.setReference(reference);
                transaction.setDateValeur(parseBiatDate(getCellStringValue(row.getCell(3))));
                transaction.setMontant(debit);  // Montant = valeur du débit

                transactionRepository.save(transaction);
                newlyAddedTransactions.add(transaction); // ✅ Ajout dans la liste des nouvelles
            }
        }

        return newlyAddedTransactions; // ✅ Retourne uniquement les nouvelles transactions
    }



    // Méthodes utilitaires
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        return cell.toString();
    }

    private Double getCellNumericValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return null;
        return cell.getNumericCellValue();
    }

    private LocalDate parseBiatDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM. yy", Locale.FRENCH);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return LocalDate.now(); // Fallback si le parsing échoue
        }
    }


}