package com.example.url_shortener.Service;


import com.example.url_shortener.Entity.Users;
import com.example.url_shortener.Repository.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserRegisterService {

    AuthenticationManager authManager ;

    private final UsersRepo repo ;

    private JWTService jwtService ;

    private final BCryptPasswordEncoder encoder  ;

    public Users register(Users user){

        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(Users user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

        if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }

        return "Failed" ;
    }
}