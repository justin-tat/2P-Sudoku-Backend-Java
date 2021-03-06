package io.twodoku.twodokuserver.controllers;

import org.springframework.web.bind.annotation.RestController;

import io.twodoku.twodokuserver.models.BodyParams;
import io.twodoku.twodokuserver.models.gameModels.Game;
import io.twodoku.twodokuserver.models.userModels.User;
import io.twodoku.twodokuserver.repository.GameInterface;
import io.twodoku.twodokuserver.repository.UserInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/users")
public class Users {

  @Autowired
  UserInterface userInterface;
  @Autowired
  GameInterface gameInterface;
  
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

  @GetMapping("/gameHistory")
  public ResponseEntity getGameHistory(@RequestParam("userId") Integer userId, @RequestParam("username") String username) {

    List<Game> orderedGames = gameInterface.findByP1IdOrP2IdOrderByTimeDesc(userId, userId);
    List<HashMap<String, Object>> parsedGames = new ArrayList<>();
    PrettyTime p = new PrettyTime();
    for (Game game : orderedGames) {
      String opponentName = game.getP1_id() == userId ? game.getP2_name() : game.getP1_name();
      int opponentRating = game.getP1_id() == userId ? game.getP2_rating() : game.getP1_rating();
      int winningId = game.getIs_finished().equals(game.getP1_name()) ? game.getP1_id() : game.getP2_id();
      boolean didWin = winningId == userId ? true : false;
      String gameTime = p.format(new Date(game.getTime().toInstant().toEpochMilli()));
      HashMap<String, Object> formattedGame = new HashMap<>();
      formattedGame.put("opponent", opponentName);
      formattedGame.put("date", gameTime);
      formattedGame.put("opponentRating", opponentRating);
      formattedGame.put("win", didWin);
      parsedGames.add(formattedGame);
    }

    return new ResponseEntity<>(parsedGames, HttpStatus.OK);

    //return ResponseEntity.status(HttpStatus.CONFLICT).body("Testing");

  }

  @PostMapping(
    value = "/makeAccount",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  public ResponseEntity makeAccount(@RequestBody BodyParams bodyParams) {
    try{
      HashMap<String, Object> params = bodyParams.getParams();
      String username = params.get("username").toString();
      String password = params.get("password").toString();
      String email = params.get("email").toString();
      userInterface.save(new User(username, email, password));
      return ResponseEntity.status(200).body("Successfully made account");
    } catch(Exception e) {
      return ResponseEntity.status(500).body("Server errored while making account");
    }
  }
  
}
