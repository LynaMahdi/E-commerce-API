package com.example.tp2_api_rest.ecommerceapi.controller;


import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // Créer une nouvelle adresse si elle n'existe pas
    @PostMapping("/create-address")
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return ResponseEntity.ok(createdAddress);
    }

    // Récupérer toutes les adresses
    @GetMapping("/all")
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    // Récupérer une adresse par ID
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        try {
            Address address = addressService.getAddressById(id);
            return ResponseEntity.ok(address);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mettre à jour une adresse par ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        try {
            Address updatedAddress = addressService.updateAddress(id, newAddress);
            return ResponseEntity.ok(updatedAddress);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer une adresse par ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        try {

            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();

        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
