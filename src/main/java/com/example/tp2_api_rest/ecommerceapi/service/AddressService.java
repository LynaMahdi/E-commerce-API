package com.example.tp2_api_rest.ecommerceapi.service;


import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.RessourceExists;
import com.example.tp2_api_rest.ecommerceapi.repository.AddressRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRespository;

    // Créer une adresse seulement si elle n'existe pas déjà
    public Address createAddress(Address address) {

        Address existingAddress = addressRepository.findByCountryAndCityAndStreet(
                address.getCountry(), address.getCity(), address.getStreet());

        if (existingAddress != null) {
            throw new RessourceExists("Address already exists");
        }
        return addressRepository.save(address);
    }


    // Récupérer toutes les adresses
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    // Récupérer une adresse par ID
    public Address getAddressById(Long id) throws NotFoundException {
        return addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("not found adress"));

    }

    public Address updateAddress(Long addressId, Address newAddress) throws NotFoundException {
        // Vérifie si une adresse similaire existe dans la base de données
        Address existingAddress = addressRepository.findByCountryAndCityAndStreet(
                newAddress.getCountry(), newAddress.getCity(), newAddress.getStreet());

        if (existingAddress == null) {
            // Si aucune adresse similaire n'est trouvée, met à jour l'adresse existante
            Address addressFromDB = addressRepository.findById(addressId)
                    .orElseThrow(() -> new NotFoundException("Address not found"));

            addressFromDB.setCountry(newAddress.getCountry());
            addressFromDB.setCity(newAddress.getCity());
            addressFromDB.setStreet(newAddress.getStreet());
            addressFromDB.setPostalCode(newAddress.getPostalCode());

            // Enregistre l'adresse mise à jour dans la base de données
            return addressRepository.save(addressFromDB);
        } else {
            // Si une adresse similaire existe, récupère d'abord l'adresse actuelle à partir de l'ID
            Address addressFromDB = addressRepository.findById(addressId)
                    .orElseThrow(() -> new NotFoundException("Address not found"));

            // Récupère les utilisateurs liés à cette adresse
            List<User> users = userRespository.findByAddresses(addressFromDB);

            // Met à jour les adresses des utilisateurs pour pointer vers l'adresse existante
            for (User user : users) {
                user.getAddresses().removeIf(address -> address.getAddress_id().equals(addressId));
                user.getAddresses().add(existingAddress);  // Ajoute l'adresse existante
            }

            // Supprime l'ancienne adresse
            deleteAddress(addressId);

            // Retourne l'adresse existante
            return existingAddress;
        }
    }




    public void deleteAddress(Long id) throws NotFoundException {
        // Vérifier si l'adresse existe
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        // Récupérer les utilisateurs liés à cette adresse
        List<User> usersWithAddress = userRespository.findByAddresses(address);

        for (User user : usersWithAddress) {
            user.setAddresses(null);  // Suppression de l'adresse de l'utilisateur
            userRespository.save(user);  // Sauvegarde des utilisateurs sans adresse
        }

        // Supprimer ensuite l'adresse
        addressRepository.delete(address);
    }



}
