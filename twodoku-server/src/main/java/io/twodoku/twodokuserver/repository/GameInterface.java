package io.twodoku.twodokuserver.repository;

import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.twodoku.twodokuserver.models.Board;
import io.twodoku.twodokuserver.models.Game;
import io.twodoku.twodokuserver.models.MadeBoard;
import io.twodoku.twodokuserver.models.PostGameIds;

public interface GameInterface extends JpaRepository<Game, Integer> {
  //Snake case is reserved in Spring. Instance variable in Game.java converted to camel-case, but still looks for snake-cased column
  List<Game> findByP1IdOrP2Id(int p1_id, int p2_id);
  List<Game> findByP1IdOrP2IdOrderByTimeDesc(int p1_id, int p2_id);
  
  @Modifying
  @Query(value = "INSERT INTO games (p1_id, p2_id, p1_name, p2_name, p1_rating, p2_rating, is_finished) VALUES (:p1Id, :p2Id, :p1Name, :p2Name, :p1Rating, :p2Rating, '') RETURNING id AS id, p1_id AS p1_id, p2_id AS p2_id  ",
  nativeQuery = true)
  List<PostGameIds> getIdsAfterInserting (@Param("p1Id") int p1Id,@Param("p2Id") int p2Id, @Param("p1Name") String p1Name, @Param("p2Name") String p2Name, @Param("p1Rating") int p1Rating, @Param("p2Rating") int p2Rating );

  @Modifying
  @Query(value = "INSERT INTO boards (player_id, game_id, board_state, board_solution, answerable_cells, holes) VALUES (:playerId, :gameId, :boardState, :boardSolution, :answerableCells, :holes) RETURNING player_id AS playerId, id AS id, board_state AS boardState, holes AS holes, answerable_cells AS answerableCells, game_id AS gameId",
  nativeQuery = true)
  List<MadeBoard> makeBoard (@Param("playerId") int playerId,@Param("gameId") int gameId, @Param("boardState") String boardState, @Param("boardSolution") String boardSolution, @Param("answerableCells") String answerableCells, @Param("holes") int holes );
  
  @Modifying
  @Query(value = "UPDATE users SET board_id = :boardId, game_id = :gameId, games_played = games_played + 1 WHERE id = :playerId",
  nativeQuery = true)
  @Transactional
  void updateUserBoards (@Param("boardId") int boardId,@Param("gameId") int gameId, @Param("playerId") int playerId);

}
