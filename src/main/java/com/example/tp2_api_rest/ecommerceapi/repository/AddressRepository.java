package com.example.tp2_api_rest.ecommerceapi.repository;


import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByCountryAndCityAndStreet(String country, String city, String street);
    Address findByStreetAndCityAndPostalCodeAndCountry(String street, String city, String postalCode, String country);
}