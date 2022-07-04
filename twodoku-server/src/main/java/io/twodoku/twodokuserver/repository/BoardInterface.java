package io.twodoku.twodokuserver.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.twodoku.twodokuserver.models.Board;
import io.twodoku.twodokuserver.models.MadeBoard;

public interface BoardInterface extends JpaRepository<Board, Integer> {
  @Modifying
  @Query(value = "INSERT INTO boards (player_id, game_id, board_state, board_solution, answerable_cells, holes) VALUES (:playerId, :gameId, :boardState, :boardSolution, :answerableCells, :holes) RETURNING player_id AS player_id, id AS id, board_state AS board_state, holes AS holes, answerable_cells AS answerable_cells, game_id AS game_id",
  nativeQuery = true)
  List<MadeBoard> makeBoard (@Param("playerId") int playerId,@Param("gameId") int gameId, @Param("boardState") String boardState, @Param("boardSolution") String boardSolution, @Param("answerableCells") String answerableCells, @Param("holes") int holes );

  @Modifying
  @Query(nativeQuery = true,
  value="UPDATE boards SET board_state = :board, player_mistakes = player_mistakes + :mistakes WHERE id = :id")
  @Transactional
  void updateBoardState(@Param("board") String boardState, @Param("mistakes") int mistakes, @Param("id") int id);

}
