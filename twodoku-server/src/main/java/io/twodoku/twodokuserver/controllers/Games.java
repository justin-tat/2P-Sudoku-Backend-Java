package io.twodoku.twodokuserver.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.script.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.graalvm.polyglot.*;

import io.twodoku.twodokuserver.repository.UserInterface;
import io.twodoku.twodokuserver.controllers.eloCalculator.EloRating;
import io.twodoku.twodokuserver.models.BodyParams;
import io.twodoku.twodokuserver.models.boardModels.Board;
import io.twodoku.twodokuserver.models.boardModels.MadeBoard;
import io.twodoku.twodokuserver.models.gameModels.FindUserIds;
import io.twodoku.twodokuserver.models.gameModels.Game;
import io.twodoku.twodokuserver.models.gameModels.PostGameIds;
import io.twodoku.twodokuserver.models.userModels.UserStats;
import io.twodoku.twodokuserver.repository.BoardInterface;
import io.twodoku.twodokuserver.repository.GameInterface;

@RestController
@RequestMapping("/games")
public class Games {
  @Autowired
  UserInterface userInterface;
  @Autowired
  GameInterface gameInterface;
  @Autowired
  BoardInterface boardInterface;

  //DONE
  @PostMapping(
    value = "/makeGame",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  public ResponseEntity makeGame(@RequestBody BodyParams bodyParams) throws ScriptException, IOException, URISyntaxException, NoSuchMethodException {
    HashMap<String, Object> params = bodyParams.getParams();
    HashMap<String, Object> playerOne = (HashMap<String, Object>) params.get("playerOne");
    HashMap<String, Object> playerTwo = (HashMap<String, Object>) params.get("playerTwo");
    //ids in order: id, p1_id, p2_id
    List<PostGameIds> ids = gameInterface.getIdsAfterInserting((Integer) playerOne.get("id"), (Integer) playerTwo.get("id"),(String) playerOne.get("name"), (String) playerTwo.get("name"), (Integer) playerOne.get("rating"), (Integer) playerTwo.get("rating"));
    
    ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
    Path jsPath = Paths.get(Games.class.getResource("jsResources/generateBoard.js").toURI());
    Invocable inv;
    Map<String, Object> board = null;
    try (BufferedReader reader = Files.newBufferedReader(jsPath);){
      graalEngine.eval(reader);
      inv = (Invocable) graalEngine;
      board = (Map<String, Object>) inv.invokeFunction("generateUniqueBoard", 1);
    } catch(Exception e) {
      System.out.println("Error when calling js function: " + e);
    }
    String boardState = board.get("1").toString().replaceAll("\\s+", "").split("\\)")[1];
    String boardSolution = board.get("2").toString().replaceAll("\\s+", "").split("\\)")[1];
    List<MadeBoard> board1_info = boardInterface.makeBoard(ids.get(0).getP1_id(), ids.get(0).getId(), boardState, boardSolution, boardState, 1);
    List<MadeBoard> board2_info = boardInterface.makeBoard(ids.get(0).getP2_id(), ids.get(0).getId(), boardState, boardSolution, boardState, 1);

    //UpdateUserBoard
    userInterface.updateUserBoards(board1_info.get(0).getId(), ids.get(0).getId(), board1_info.get(0).getPlayerId());
    userInterface.updateUserBoards(board2_info.get(0).getId(), ids.get(0).getId(), board2_info.get(0).getPlayerId());
    Map<String, Object> toUsers = new HashMap<>();
    toUsers.put("boardState", boardState);
    toUsers.put("boardSolution", boardSolution);
    toUsers.put("holes", 1);
    toUsers.put("game_id", ids.get(0).getId());
    toUsers.put("board_id", board1_info.get(0).getId());
    toUsers.put("opp_board_id", board2_info.get(0).getId());
    return ResponseEntity.status(200).body(toUsers);
  }

  //DONE
  @GetMapping("/getGame")
  public ResponseEntity getGame(@RequestParam("boardId") int boardId, @RequestParam("userId") int userId) {
    Board board = boardInterface.findById(boardId).get();
    String isFinished = gameInterface.findById(board.getGame_id()).get().getIs_finished();
    if (isFinished.length() != 0) {
      userInterface.updateUserIds(userId);
      return ResponseEntity.status(200).body("You lost");
    }
    return ResponseEntity.status(200).body(board);
  }

  //DONE
  @PutMapping(
    value = "/updateGame",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  public ResponseEntity updateGame(@RequestBody BodyParams bodyParams) {
    HashMap<String, Object> params = bodyParams.getParams();
    String board = params.get("boardState").toString().replaceAll("\\s+", "");
    HashMap<Integer, Boolean> incorrectTiles = (HashMap<Integer, Boolean>)params.get("incorrectTiles");
    int boardId = Integer.parseInt((String) params.get("boardId"));
    boardInterface.updateBoardState(board, incorrectTiles.keySet().size(), boardId);
    return ResponseEntity.status(500).body("Successfully updated Game");
  }

//Shove String isFinished creation to the top to check if the dbs need to be updated at all before getting anything else. If game has already been decided, you just need to update the asking player's info in users and rating
  @PutMapping("/finishGame")
  public ResponseEntity finishGame(@RequestBody BodyParams bodyParams) {
    HashMap<String, Object> params = bodyParams.getParams();
    int gameId = Integer.parseInt((String) params.get("gameId"));
    String winStatus = gameInterface.getIs_finished(gameId).equals("") ? "You won" : "You lost";
    int boardId = Integer.parseInt((String) params.get("boardId"));
    int askingId = Integer.parseInt((String) params.get("userId"));
    //pr1 userIdsPromise
    FindUserIds userIds = gameInterface.findUserIds(gameId);
    //pr2 userStats
    UserStats p1Stats = userInterface.getUserStats(userIds.getP1_id());
    UserStats p2Stats = userInterface.getUserStats(userIds.getP2_id());
    //pr3 arr
    int askingRating = 0;
    int waitingRating = 0;
    String askerName = "";
    int askerId = 0;
    int waitingId = 0;
    if (askingId == userIds.getP1_id()) {
      askingRating = p1Stats.getRating();
      askerName = p1Stats.getName();
      waitingRating = p2Stats.getRating();
      askerId = userIds.getP1_id();
      waitingRating = userIds.getP2_id();
    } else {
      askingRating = p2Stats.getRating();
      askerName = p2Stats.getName();
      waitingRating = p1Stats.getRating();
      askerId = userIds.getP2_id();
      waitingRating = userIds.getP1_id();
    }
    EloRating elo = new EloRating();
    int newAskingRating = 0;
    int newWaitingRating = 0;
    
    //Update board and gameId when you realize, but update rating when the winner actually wins
    //Could create an additional column called "trueRating". When somebody wins, change their rating and trueRating to the result of using the elo variable, but only change the trueRating of the waitingPerson. Then, when the loser finds out they've lost, update their rating to the trueRating. This will allow losers who don't know they've lost to still have their old rating when going into their gameHistory.
    if (winStatus.equals("You lost")) {
      userInterface.updateUserIds(askerId);
      newAskingRating = elo.calculate2PlayersRating(askingRating, waitingRating, "-");
    } else {
      newAskingRating = elo.calculate2PlayersRating(askingRating, waitingRating, "+");
      newWaitingRating = elo.calculate2PlayersRating(waitingRating, askingRating, "-");
      userInterface.updateUserIds(askerId);
      gameInterface.updateFinishedGame(askerName, gameId);
      userInterface.updateRating(askerId, newAskingRating);
      userInterface.updateRating(waitingId, newWaitingRating);
    }
    HashMap<String, Object> response = new HashMap<>();
    response.put("isFinished", winStatus);
    response.put("newRating", newAskingRating);
    return ResponseEntity.status(200).body(response);
  }
  
}
