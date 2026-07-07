package com.example.url_shortener.Repository;

import com.example.url_shortener.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<Users , Long> {
    Users findByUsername(String username);
}
