package com.birozsombor4.springrestapitemplate.avatar;

import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

  String renameAndStore(MultipartFile file, Integer userId) throws IOException;

  Resource loadAvatarAsResource(String filename) throws MalformedURLException;

  void deleteAvatar(String filename);

  void deleteAllCustomAvatar();

  String getAvatarContentType(String filename);
}
