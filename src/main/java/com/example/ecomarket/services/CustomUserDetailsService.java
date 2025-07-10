package com.example.ecomarket.services;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginRepository loginRepository;

    public CustomUserDetailsService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String rut) throws UsernameNotFoundException {
        LoginModel user = loginRepository.findByRut(rut)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con RUT: " + rut));

        return user;
    }
}