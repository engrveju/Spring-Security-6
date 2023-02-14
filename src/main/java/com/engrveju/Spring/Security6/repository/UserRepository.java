package com.engrveju.Spring.Security6.repository;

import com.engrveju.Spring.Security6.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
//  Optional<User> findByToken(String token);
  Boolean existsByEmail(String Email);

}
