package com.example.wholesalesalesbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wholesalesalesbackend.dto.ClientCreateRequest;
import com.example.wholesalesalesbackend.model.Client;
import com.example.wholesalesalesbackend.repository.ClientRepository;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client addClient(Client client) {
        if (clientRepository.existsByName(client.getName())) {
            throw new RuntimeException("Client already exists with name: " + client.getName());
        }
        return clientRepository.save(client);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public Client getClientByName(String name) {
        return clientRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Client not found with name: " + name));
    }

    public Client updateClient(Long id, ClientCreateRequest request) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        existing.setName(request.getName());
        existing.setLocation(request.getLocation());
        existing.setContact(request.getContact());

        return clientRepository.save(existing);
    }

    public String deleteClient(Long id) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        clientRepository.delete(existing);
        return "Deleted !!!";
    }

}
