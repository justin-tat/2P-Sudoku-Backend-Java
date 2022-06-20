package io.twodoku.twodokuserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.twodoku.twodokuserver.models.User;

public interface UserInterface extends JpaRepository<User, Long> {
  User findByName(String name);
  User findByEmail(String email);
}
