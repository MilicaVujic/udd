package com.example.udd_security_incidents.controller;


import com.example.udd_security_incidents.dto.AuthMessage;
import com.example.udd_security_incidents.dto.LoginRequest;
import com.example.udd_security_incidents.dto.RegisterRequest;
import com.example.udd_security_incidents.model.User;
import com.example.udd_security_incidents.service.JwtTokenProvider;
import com.example.udd_security_incidents.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private  UserDetailsServiceImpl userDetailsService;
    @Autowired
    private  JwtTokenProvider jwtTokenProvider;
    @Autowired
    private  PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user=userDetailsService.saveUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthMessage> login(@RequestBody LoginRequest request) {
        var user = userDetailsService.loadUserByUsername(request.getUsername());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtTokenProvider.generateToken(user.getUsername());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(new AuthMessage("LOGGED IN"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthMessage("INVALID CREDENTIALS"));
    }


}
