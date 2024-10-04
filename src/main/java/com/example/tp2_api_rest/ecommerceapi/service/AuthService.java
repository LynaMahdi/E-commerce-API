package com.example.tp2_api_rest.ecommerceapi.service;


import com.example.tp2_api_rest.ecommerceapi.entity.Address;
import com.example.tp2_api_rest.ecommerceapi.entity.Cart;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.RessourceExists;
import com.example.tp2_api_rest.ecommerceapi.repository.AddressRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.CartRepository;
import com.example.tp2_api_rest.ecommerceapi.responses.AuthResponse;
import com.example.tp2_api_rest.ecommerceapi.responses.LoginRequest;
import com.example.tp2_api_rest.ecommerceapi.responses.RegisterRequest;
import com.example.tp2_api_rest.ecommerceapi.jwt.JwtService;
import com.example.tp2_api_rest.ecommerceapi.jwt.TokenRefreshRequest;
import com.example.tp2_api_rest.ecommerceapi.repository.UserRepository;
import com.example.tp2_api_rest.ecommerceapi.entity.Role;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRespository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userRespository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Générer le token d'accès et le refresh token
        String accessToken = jwtService.getToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);  // Générer le refresh token

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {

        // Vérifier si un utilisateur avec cet email existe déjà
        Optional<User> existingUser = userRespository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new RessourceExists("User already exists with email: " + request.getEmail());
        }


            // Créer l'utilisateur à partir de la requête
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();

            // Créer un panier pour l'utilisateur
            Cart cart = new Cart();
            user.setCart(cart);
            // Vérifier si une adresse est fournie
            if (request.getAddress() != null) {
                String country = request.getAddress().getCountry();
                String city = request.getAddress().getCity();
                String street = request.getAddress().getStreet();

                // Vérifier si l'adresse existe dans la base de données
                Address address = addressRepository.findByCountryAndCityAndStreet(
                        country, city, street
                );

                // Si l'adresse n'existe pas, la créer
                if (address == null) {
                    address = new Address();
                    address.setCountry(country);
                    address.setCity(city);
                    address.setStreet(street);
                    address.setPostalCode(request.getAddress().getPostalCode());

                    address = addressRepository.save(address);  // Sauvegarde l'adresse dans la base
                }

                // Associer l'adresse à l'utilisateur
                user.setAddresses(List.of(address));
            }

            // Sauvegarder l'utilisateur
            User registeredUser = userRespository.save(user);
            System.out.println("je suis leuser cree "+ registeredUser);
            // Associer le panier à l'utilisateur


            cart.setUser(registeredUser);
            cart.setTotalPrice(0.0);
            cart = cartRepository.save(cart);
            // Générer les tokens
            String accessToken = jwtService.getToken(registeredUser);
            String refreshToken = jwtService.generateRefreshToken(registeredUser);

            // Retourner la réponse avec les tokens
            return AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .build();

    }



    public User updateUser(Integer userId, RegisterRequest request) throws NotFoundException {
        // Retrieve the user by ID
        User user = userRespository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Update user properties
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // If a new password is provided, encode and set it
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Check if an address is provided
        if (request.getAddress() != null) {
            String country = request.getAddress().getCountry();
            String city = request.getAddress().getCity();
            String street = request.getAddress().getStreet();

            // Check if the address already exists in the database
            Address address = addressRepository.findByCountryAndCityAndStreet(country, city, street);

            // If the address does not exist, create a new one
            if (address == null) {
                address = new Address();
                address.setCountry(country);
                address.setCity(city);
                address.setStreet(street);
                address.setPostalCode(request.getAddress().getPostalCode());
                address = addressRepository.save(address); // Save the new address
            }

            // Update the user's addresses with a mutable list
            List<Address> updatedAddresses = new ArrayList<>();
            updatedAddresses.add(address);
            user.setAddresses(updatedAddresses);
        }

        // Save the updated user
        return userRespository.save(user);
    }


    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRespository.findAll().forEach(users::add);

        return users;
    }


    // Rafraîchir le token
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.getUsernameFromToken(refreshToken);

        UserDetails user = userRespository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Vérifier si le refresh token est valide
        if (jwtService.isTokenValid(refreshToken, user)) {
            String newAccessToken = jwtService.getToken(user);
            return AuthResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        throw new RuntimeException("Refresh token invalide");
    }

}
