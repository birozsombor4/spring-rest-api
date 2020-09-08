package com.birozsombor4.springrestapitemplate.services;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import java.net.MalformedURLException;

public interface UserService {

  void updateUser(User user);

  UserDTO convertUserToUserDTO(User saveUser);

  User convertUserDTOToUser(UserDTO userDTO);

  User getUserByUsername(String username);

  User getUserById(Integer userId);

  String getAuthenticatedUsername();

  void attachAvatarToUser(User user, String filename) throws MalformedURLException;
}