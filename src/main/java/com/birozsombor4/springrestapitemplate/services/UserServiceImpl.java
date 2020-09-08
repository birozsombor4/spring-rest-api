package com.birozsombor4.springrestapitemplate.services;

import com.birozsombor4.springrestapitemplate.avatar.AvatarService;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileLoadingException;
import com.birozsombor4.springrestapitemplate.exceptions.UserNotFoundException;
import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.models.dtos.UserDTO;
import com.birozsombor4.springrestapitemplate.respositories.UserRepository;
import java.net.MalformedURLException;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;
  private AvatarService avatarService;

  @Autowired
  public UserServiceImpl(UserRepository userRepository,
                         AvatarService avatarService) {
    this.userRepository = userRepository;
    this.avatarService = avatarService;
  }

  @Override
  public UserDTO convertUserToUserDTO(User user) {
    UserDTO userDTO = new UserDTO();
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.map(user, userDTO);
    return userDTO;
  }

  @Override
  public User convertUserDTOToUser(UserDTO userDTO) {
    User user = new User();
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.map(userDTO, user);
    return user;
  }

  @Override
  public User getUserByUsername(String username) {
    Optional<User> optionalUser = userRepository.findByUsername(username);
    return optionalUser.orElseThrow(() -> new UsernameNotFoundException(username));
  }

  @Override
  public void updateUser(User user) {
    userRepository.save(user);
  }

  @Override
  public User getUserById(Integer userId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    return optionalUser.orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
  }

  @Override
  public String getAuthenticatedUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  @Override
  public void attachAvatarToUser(User user, String filename) throws MalformedURLException {
    if (avatarService.loadAvatarAsResource(filename) != null && avatarService.loadAvatarAsResource(filename).exists()) {
      user.setAvatar(filename);
    } else {
      throw new FailedFileLoadingException(filename);
    }
  }
}