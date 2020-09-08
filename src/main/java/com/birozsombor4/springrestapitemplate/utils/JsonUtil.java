package com.birozsombor4.springrestapitemplate.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

  public static String convertObjectToJson(Object pojo) {
    ObjectMapper objectMapper = new ObjectMapper();
    String objectAsJson = null;
    try {
      objectAsJson = objectMapper.writeValueAsString(pojo);
    } catch (JsonProcessingException e) {
      System.out.println("Something went wrong with converting object to JSON.");
    }
    return objectAsJson;
  }
}
