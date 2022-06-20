package io.twodoku.twodokuserver.controllers;

import org.springframework.web.bind.annotation.RestController;

import io.twodoku.twodokuserver.models.User;
import io.twodoku.twodokuserver.repository.UserInterface;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<User> getAccount(@RequestParam("username") String username, @RequestParam("password") String password) {
    try{
      User user = userInterface.findByName(username);
      if (user.getPassword().equals(password)) {
        System.out.println("Found password matches ");
        return new ResponseEntity<>(user, HttpStatus.OK);
      }
      System.out.println("Found passwords do not match Given: " + password + " || Expecting: " + user.getPassword());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch(Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Map<String, String> response = new HashMap<>();
    // response.put("name", username);
    // response.put("password", password);
    // return response;
  }
  
}
