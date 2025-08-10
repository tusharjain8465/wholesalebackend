package com.example.wholesalesalesbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.wholesalesalesbackend.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByMail(String username);

    Optional<User> findByUsernameOrMobileNumberOrMail(String userName, Long mobileNumber, String mail);

}
