package io.twodoku.twodokuserver.models.userModels;

public interface UserStats {
  public Long getGames_played();
  public Integer getHighest_rating();
  public Integer getRating();
  public String getName();
}
