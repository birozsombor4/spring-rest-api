package com.birozsombor4.springrestapitemplate.avatar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AvatarConfig {

  @Value("${avatars.root.location}")
  private String location;

  public AvatarConfig() {
  }

  public AvatarConfig(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
