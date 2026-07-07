package com.example.url_shortener.Controller;

import com.example.url_shortener.Entity.Users;
import com.example.url_shortener.Service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegisterController {

    @Autowired
    private UserRegisterService userRegister ;

    @PostMapping("/register")
    public Users userRegister(@RequestBody Users user){

        return userRegister.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user){

        return userRegister.verify(user) ;
    }
}
