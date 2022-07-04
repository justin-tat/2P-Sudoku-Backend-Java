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
import io.twodoku.twodokuserver.models.Board;
import io.twodoku.twodokuserver.models.BodyParams;
import io.twodoku.twodokuserver.models.FindUserIds;
import io.twodoku.twodokuserver.models.Game;
import io.twodoku.twodokuserver.models.MadeBoard;
import io.twodoku.twodokuserver.models.PostGameIds;
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

  //Make sure that you are sending boardId after creation for both of the users. See most recent changes in GH
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
    System.out.println("boardId: " + boardId);
    System.out.println("board: " + board);
    boardInterface.updateBoardState(board, incorrectTiles.keySet().size(), boardId);
    return ResponseEntity.status(500).body("Successfully updated Game");
  }

  @PutMapping("/finishGame")
  public ResponseEntity finishGame(@RequestBody BodyParams bodyParams) {
    HashMap<String, Object> params = bodyParams.getParams();
    int boardId = Integer.parseInt((String) params.get("boardId"));
    int gameId = Integer.parseInt((String) params.get("gameId"));
    List<FindUserIds> userIds = gameInterface.findUserIds(gameId);
    return ResponseEntity.status(500).body(userIds);
  }
  
}
