package io.twodoku.twodokuserver.models;

import java.time.OffsetDateTime;

import javax.persistence.*;

@Entity
@Table(name = "boards")
public class Board {
  @Id
  @Column(name="id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name="player_id")
  private int playerId;
  @Column(name="game_id")
  private int gameId;
  @Column(name="gametime")
  private OffsetDateTime gameTime = OffsetDateTime.now();
  @Column(name="holes")
  private int holes = 0;
  @Column(name="player_mistakes")
  private short playerMistakes = 0;
  @Column(name="board_state")
  private String boardState;
  @Column(name="board_solution")
  private String boardSolution;
  @Column(name="answerable_cells")
  private String answerableCells = "";



  public Board(Integer id, int playerId, int gameId, int holes, short playerMistakes, String boardState,
      String boardSolution, OffsetDateTime time, String answerableCells) {
    this.id = id;
    this.playerId = playerId;
    this.gameId = gameId;
    this.holes = holes;
    this.playerMistakes = playerMistakes;
    this.boardState = boardState;
    this.boardSolution = boardSolution;
    this.gameTime = time;
    this.answerableCells = answerableCells;
  }

  Board() {}

  public Integer getId() {
    return id;
  }

  public int getPlayerId() {
    return playerId;
  }
  public int getGameId() {
    return gameId;
  }
  public OffsetDateTime getGameTime() {
    return gameTime;
  }
  public int getHoles() {
    return holes;
  }
  public short getPlayerMistakes() {
    return playerMistakes;
  }
  public String getBoardState() {
    return boardState;
  }
  public String getBoardSolution() {
    return boardSolution;
  }
  public String getAnswerableCells() {
    return answerableCells;
  }

  
  
}
