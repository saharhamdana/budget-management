package com.onetech.budget.controllers;

import com.onetech.budget.models.Client;
import com.onetech.budget.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;
    @Autowired
    public ClientController(ClientService clientService ) {
        this.clientService = clientService;
    }
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client savedClient = this.clientService.saveClient(client);
        return ResponseEntity.ok(savedClient);
    }

    @GetMapping
    public List<Client> getAllClients() {
        return this.clientService.findAll();
    }
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        this.clientService.deleteById(id);
    }

}