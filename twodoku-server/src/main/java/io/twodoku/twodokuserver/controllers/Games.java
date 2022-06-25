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

import javax.script.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.twodoku.twodokuserver.repository.UserInterface;
import io.twodoku.twodokuserver.models.BodyParams;
import io.twodoku.twodokuserver.models.Game;
import io.twodoku.twodokuserver.repository.GameInterface;

@RestController
@RequestMapping("/games")
public class Games {
  @Autowired
  UserInterface userInterface;
  @Autowired
  GameInterface gameInterface;

  @PostMapping(
    value = "/makeGame",
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  public ResponseEntity makeGame(@RequestBody BodyParams bodyParams) throws ScriptException, IOException, URISyntaxException, NoSuchMethodException {
    HashMap<String, Object> params = bodyParams.getParams();
    HashMap<String, Object> playerOne = (HashMap<String, Object>) params.get("playerOne");
    HashMap<String, Object> playerTwo = (HashMap<String, Object>) params.get("playerTwo");
    String name = (String) playerOne.get("name");
    System.out.println("name: " + name);
    //ids in order: id, p1_id, p2_id
    List<Object> ids = gameInterface.getIdsAfterInserting((Integer) playerOne.get("id"), (Integer) playerTwo.get("id"),(String) playerOne.get("name"), (String) playerTwo.get("name"), (Integer) playerOne.get("rating"), (Integer) playerTwo.get("rating"));
    ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
    System.out.println(Games.class.getResource("jsResources/generateBoard.js").toURI());
    Path jsPath = Paths.get(Games.class.getResource("jsResources/generateBoard.js").toURI());
    try (BufferedReader reader = Files.newBufferedReader(jsPath);){
      //ScriptEngineManager manager = new ScriptEngineManager();
      //ScriptEngine engine = manager.getEngineByName("JavaScript");

      //NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
      //ScriptEngine engine = factory.getScriptEngine("--language=es6");
      //String filePath = "twodoku-server/src/main/java/io/twodoku/twodokuserver/jsResources/generateBoard.js";
      //File file = new File(filePath);
      System.out.println("Just created file with filePath " + jsPath);
      
      //Invocable inv = (Invocable) engine;
      //engine.eval(Files.newBufferedReader(file.getAbsolutePath()));
      System.out.println("Created reader: ");
      System.out.println("graalEngine: " + graalEngine);
      graalEngine.eval(reader);
      System.out.println("Evaluated the file using graalEngine");
      Invocable inv = (Invocable) graalEngine;
      
      System.out.println(inv.invokeFunction("found"));
      System.out.println("Evaluated the file");
    } catch(Exception e) {
      System.out.println("Error when calling js function: " + e);
      System.out.println(e);
    }
    return ResponseEntity.status(200).body(ids);
  }
}
