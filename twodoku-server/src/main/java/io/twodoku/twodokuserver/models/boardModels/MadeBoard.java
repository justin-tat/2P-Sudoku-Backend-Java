package io.twodoku.twodokuserver.models.boardModels;

public interface MadeBoard {
  public Integer getId();
  public Integer getPlayerId();
  public String getBoardState();
  public Integer getHoles();
  public String getAnswerableCells();
  public Integer getGameId();
}
