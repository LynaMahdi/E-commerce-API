package com.example.tp2_api_rest.ecommerceapi.responses;

import com.example.tp2_api_rest.ecommerceapi.responses.AddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfile {
    private String username;
    private String email;
    private List<AddressDTO> addresses;
    // Constructeurs, getters et setters
}

