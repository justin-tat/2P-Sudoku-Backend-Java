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
  private int game_id;
  @Column(name="gametime")
  private OffsetDateTime gametime = OffsetDateTime.now();
  @Column(name="holes")
  private int holes = 0;
  @Column(name="player_mistakes")
  private short player_mistakes = 0;
  @Column(name="board_state")
  private String board_state;
  @Column(name="board_solution")
  private String board_solution;
  @Column(name="answerable_cells")
  private String answerable_cells = "";



  public Board(Integer id, int playerId, int gameId, int holes, short playerMistakes, String boardState,
      String boardSolution, OffsetDateTime time, String answerableCells) {
    this.id = id;
    this.playerId = playerId;
    this.game_id = gameId;
    this.holes = holes;
    this.player_mistakes = playerMistakes;
    this.board_state = boardState;
    this.board_solution = boardSolution;
    this.gametime = time;
    this.answerable_cells = answerableCells;
  }

  Board() {}

  public Integer getId() {
    return id;
  }

  public int getPlayer_id() {
    return playerId;
  }
  public int getGame_id() {
    return game_id;
  }
  public OffsetDateTime getGametime() {
    return gametime;
  }
  public int getHoles() {
    return holes;
  }
  public short getPlayer_mistakes() {
    return player_mistakes;
  }
  public String getBoard_state() {
    return board_state;
  }
  public String getBoard_solution() {
    return board_solution;
  }
  public String getAnswerable_cells() {
    return answerable_cells;
  }

  
  
}
