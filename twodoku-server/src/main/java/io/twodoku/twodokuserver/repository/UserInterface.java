package io.twodoku.twodokuserver.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.twodoku.twodokuserver.models.User;

public interface UserInterface extends JpaRepository<User, Integer> {
  User findByName(String name);
  User findByEmail(String email);
  @Modifying
  @Query(value = "UPDATE users SET board_id = :boardId, game_id = :gameId, games_played = games_played + 1 WHERE id = :playerId",
  nativeQuery = true)
  @Transactional
  void updateUserBoards (@Param("boardId") int boardId,@Param("gameId") int gameId, @Param("playerId") int playerId);
}
