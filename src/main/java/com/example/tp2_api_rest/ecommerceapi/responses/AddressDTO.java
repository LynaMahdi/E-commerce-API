package com.example.tp2_api_rest.ecommerceapi.responses;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AddressDTO {
    private String street;
    private String city;
    private String postalCode;
    private String country;

    public AddressDTO() {

    }


    // Constructeurs, getters et setters
}
