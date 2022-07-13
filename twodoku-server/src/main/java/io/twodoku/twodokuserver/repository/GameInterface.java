package io.twodoku.twodokuserver.repository;

import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.twodoku.twodokuserver.models.gameModels.FindUserIds;
import io.twodoku.twodokuserver.models.gameModels.Game;
import io.twodoku.twodokuserver.models.gameModels.PostGameIds;

public interface GameInterface extends JpaRepository<Game, Integer> {
  //Snake case is reserved in Spring. Instance variable in Game.java converted to camel-case, but still looks for snake-cased column
  List<Game> findByP1IdOrP2Id(int p1_id, int p2_id);
  List<Game> findByP1IdOrP2IdOrderByTimeDesc(int p1_id, int p2_id);
  
  @Modifying
  @Query(value = "INSERT INTO games (p1_id, p2_id, p1_name, p2_name, p1_rating, p2_rating, is_finished) VALUES (:p1Id, :p2Id, :p1Name, :p2Name, :p1Rating, :p2Rating, '') RETURNING id AS id, p1_id AS p1_id, p2_id AS p2_id  ",
  nativeQuery = true)
  List<PostGameIds> getIdsAfterInserting (@Param("p1Id") int p1Id,@Param("p2Id") int p2Id, @Param("p1Name") String p1Name, @Param("p2Name") String p2Name, @Param("p1Rating") int p1Rating, @Param("p2Rating") int p2Rating );

  @Query(value = "SELECT p1_id AS p1_id, p2_id AS p2_id FROM games WHERE id = :id", 
  nativeQuery = true)
  FindUserIds  findUserIds(@Param("id") int id);

  @Query(value = "SELECT is_finished FROM games WHERE id = :id", nativeQuery = true)
  String getIs_finished(@Param("id") int id);

  @Modifying
  @Query(value = "UPDATE games SET is_finished = :winner WHERE id = :gameId", nativeQuery = true)
  @Transactional
  void updateFinishedGame(@Param("winner") String winner, @Param("gameId") int gameId);
}
