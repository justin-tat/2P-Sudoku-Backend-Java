package io.twodoku.twodokuserver.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.twodoku.twodokuserver.models.userModels.User;
import io.twodoku.twodokuserver.models.userModels.UserStats;

public interface UserInterface extends JpaRepository<User, Integer> {
  User findByName(String name);
  User findByEmail(String email);
  @Modifying
  @Query(value = "UPDATE users SET board_id = :boardId, game_id = :gameId, games_played = games_played + 1 WHERE id = :playerId",
  nativeQuery = true)
  @Transactional
  void updateUserBoards (@Param("boardId") int boardId,@Param("gameId") int gameId, @Param("playerId") int playerId);

  @Modifying
  @Query(value = "UPDATE users SET board_id = 0, game_id = 0 WHERE id = :playerId",
  nativeQuery = true)
  @Transactional
  void updateUserIds (@Param("playerId") int playerId);

  @Query(value = "SELECT games_played AS games_played, highest_rating AS highest_rating, rating AS rating, name AS name FROM users WHERE id = :id", 
  nativeQuery = true)
  UserStats getUserStats(@Param("id") int id);

  @Modifying
  @Query(value = "UPDATE users SET rating = :rating, highest_rating = CASE WHEN $3 > highest_rating THEN :rating ELSE highest_rating END WHERE id = :playerId",
  nativeQuery = true)
  @Transactional
  void updateRating (@Param("playerId") int playerId, @Param("rating") int rating);
}
