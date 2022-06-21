package io.twodoku.twodokuserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.twodoku.twodokuserver.models.Game;

public interface GameInterface extends JpaRepository<Game, Integer> {
  //Snake case is reserved in Spring. Instance variable in Game.java converted to camel-case, but still looks for snake-cased column
  List<Game> findByP1IdOrP2Id(int p1_id, int p2_id);
  List<Game> findByP1IdOrP2IdOrderByTimeDesc(int p1_id, int p2_id);
}
