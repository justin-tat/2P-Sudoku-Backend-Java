package io.twodoku.twodokuserver.models;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User {
  @Id
  @Column(name="id")
  private Integer id;
  @Column(name="name")
  private String name;
  @Column(name="email")
  private String email;
  @Column(name="password")
  private String password;
  @Column(name="rating")
  private int rating;
  @Column(name="difficulty")
  private String difficulty;
  @Column(name="board_id")
  private long board_id;
  @Column(name="game_id")
  private long game_id;
  @Column(name="games_played")
  private long games_played;
  @Column(name="highest_rating")
  private int highest_rating;

  public User() {}

  public User(Integer id, String name, String email, String password, int rating, String difficulty, long board_id,
      long game_id, long games_played, int highest_rating) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.rating = rating;
    this.difficulty = difficulty;
    this.board_id = board_id;
    this.game_id = game_id;
    this.games_played = games_played;
    this.highest_rating = highest_rating;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  public int getRating() {
    return rating;
  }
  public void setRating(int rating) {
    this.rating = rating;
  }

  public long getBoard_id() {
    return board_id;
  }
  public void setBoard_id(long board_id) {
    this.board_id = board_id;
  }

  public long getGame_id() {
    return game_id;
  }
  public void setGame_id(long game_id) {
    this.game_id = game_id;
  }

  public long getGames_played() {
    return games_played;
  }
  public void setGames_played(long games_played) {
    this.games_played = games_played;
  }

  public int getHighest_rating() {
    return highest_rating;
  }
  public void setHighest_rating(int highest_rating) {
    this.highest_rating = highest_rating;
  }

}

