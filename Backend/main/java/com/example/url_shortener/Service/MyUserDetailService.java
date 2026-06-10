package com.example.url_shortener.Service;


import com.example.url_shortener.Entity.UserPrincipal;
import com.example.url_shortener.Entity.Users;
import com.example.url_shortener.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UsersRepo repo ;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = repo.findByUsername(username) ;

        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        return new UserPrincipal(user);
    }
}