package io.twodoku.twodokuserver.models;

import java.time.OffsetDateTime;

import javax.persistence.*;

@Entity
@Table(name="games")
public class Game {
  @Id
  @Column(name="id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name="p1_id")
  private int p1Id;
  @Column(name="p2_id")
  private int p2Id;
  @Column(name="p1_name")
  private String p1_name;
  @Column(name="p2_name")
  private String p2_name;
  @Column(name="p1_rating")
  private int p1_rating;
  @Column(name="p2_rating")
  private int p2_rating;
  @Column(name="time")
  private OffsetDateTime time;
  @Column(name="is_finished")
  private String is_finished;

  public Game() {}

  public Game(Integer id, int p1_id, int p2_id, String p1_name, String p2_name, int p1_rating, int p2_rating, OffsetDateTime time,
      String is_finished) {
    this.id = id;
    this.p1Id = p1_id;
    this.p2Id = p2_id;
    this.p1_name = p1_name;
    this.p2_name = p2_name;
    this.p1_rating = p1_rating;
    this.p2_rating = p2_rating;
    this.time = time;
    this.is_finished = is_finished;
  }

  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  public int getP1_id() {
    return p1Id;
  }
  public void setP1_id(int p1_id) {
    this.p1Id = p1_id;
  }

  public int getP2_id() {
    return p2Id;
  }
  public void setP2_id(int p2_id) {
    this.p2Id = p2_id;
  }

  public String getP1_name() {
    return p1_name;
  }
  public void setP1_name(String p1_name) {
    this.p1_name = p1_name;
  }

  public String getP2_name() {
    return p2_name;
  }
  public void setP2_name(String p2_name) {
    this.p2_name = p2_name;
  }

  public int getP1_rating() {
    return p1_rating;
  }
  public void setP1_rating(int p1_rating) {
    this.p1_rating = p1_rating;
  }

  public int getP2_rating() {
    return p2_rating;
  }
  public void setP2_rating(int p2_rating) {
    this.p2_rating = p2_rating;
  }

  public OffsetDateTime getTime() {
    return time;
  }
  public void setTime(OffsetDateTime time) {
    this.time = time;
  }

  public String getIs_finished() {
    return is_finished;
  }
  public void setIs_finished(String is_finished) {
    this.is_finished = is_finished;
  }
}
