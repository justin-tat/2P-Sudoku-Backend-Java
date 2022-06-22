package io.twodoku.twodokuserver.models;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BodyParams {

  @JsonProperty("params")
  HashMap<String, Object> params;

  BodyParams() {}

  BodyParams(HashMap<String, Object> params) {
    this.params = params;
  }

  public HashMap<String, Object> getParams() {
    return this.params;
  }
  
}
