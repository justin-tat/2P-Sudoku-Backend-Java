package io.twodoku.twodokuserver.controllers;

import org.springframework.web.bind.annotation.RestController;

import io.twodoku.twodokuserver.models.User;
import io.twodoku.twodokuserver.repository.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/users")
public class Users {

  @Autowired
  UserInterface userInterface;
  
  @GetMapping(value="/")
  public String getUsers() {
  //public String getUsers(@RequestParam String param) {
      System.out.println("You have found the java route");
      return "You have found the java route";
  }

  @GetMapping("/getAccount")
  public ResponseEntity getAccount(@RequestParam("username") String username, @RequestParam("password") String password) {
    try{
      User user = userInterface.findByName(username);
      if (user == null) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username not found");
      }
      if (user.getPassword().equals(password)) {
        System.out.println("Found password matches ");
        return new ResponseEntity<>(user, HttpStatus.OK);
      }
      System.out.println("Found passwords do not match Given: " + password + " || Expecting: " + user.getPassword());
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Password did not match that account");
    } catch(Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/verifyAccount")
  public ResponseEntity verifyAccount(@RequestParam("username") String username, @RequestParam("email") String email) {
    User user = userInterface.findByName(username);
    if (user != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken");
    }
    user = userInterface.findByEmail(email);
    if (user != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email has an account");
    }
    return ResponseEntity.status(200).body("You're big chillin");
  }
  
}
