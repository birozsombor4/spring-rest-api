package com.birozsombor4.springrestapitemplate.avatar;

import com.birozsombor4.springrestapitemplate.exceptions.FailedDirectoryCreatingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileDeletingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileLoadingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileSavingException;
import com.birozsombor4.springrestapitemplate.exceptions.UnsupportedContentTypeException;
import com.birozsombor4.springrestapitemplate.exceptions.UnsupportedFileNameException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarServiceImpl implements AvatarService {

  private AvatarConfig avatarConfig;
  private Path rootLocation;

  @Autowired
  public AvatarServiceImpl(AvatarConfig avatarConfig) {
    this.avatarConfig = avatarConfig;
    rootLocation = Paths.get(this.avatarConfig.getLocation());
    initAvatarsDirectory();
  }

  @Override
  public String renameAndStore(MultipartFile file, Integer userId) {
    if (!checkContentType(file.getContentType())) {
      throw new UnsupportedContentTypeException(file.getContentType());
    }
    if (!file.getOriginalFilename().contains(".")) {
      throw new UnsupportedFileNameException("Filename should has extension.");
    }
    try {
      InputStream inputStream = file.getInputStream();
      String newFileName = userId + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
      Files.copy(inputStream, rootLocation.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);
      return newFileName;
    } catch (IOException e) {
      throw new FailedFileSavingException(file.getOriginalFilename());
    }
  }

  @Override
  public Resource loadAvatarAsResource(String filename) {
    if (Files.exists(rootLocation.resolve(filename))) {
      Path path = rootLocation.resolve(filename);
      Resource resource = null;
      try {
        resource = new UrlResource(path.toUri());
      } catch (MalformedURLException e) {
        throw new FailedFileLoadingException(filename);
      }
      return resource;
    } else {
      throw new FailedFileLoadingException(filename);
    }
  }

  @Override
  public void deleteAvatar(String filename) {
    Path file = rootLocation.resolve(filename);
    try {
      Files.delete(file);
    } catch (IOException e) {
      throw new FailedFileDeletingException(filename);
    }
  }

  @Override
  public void deleteAllCustomAvatar() {
    for (File file : rootLocation.toFile().listFiles()) {
      if (!file.getName().contains("default")) {
        file.delete();
      }
    }
  }

  @Override
  public String getAvatarContentType(String filename) {
    if (filename.contains(".png")) {
      return "image/png";
    } else if (filename.contains("jpg") || filename.contains("jpeg")) {
      return "jpeg/png";
    } else if (filename.contains("JPG") || filename.contains("JPEG")) {
      return "image/jpeg";
    } else {
      throw new UnsupportedContentTypeException(filename);
    }
  }

  private void initAvatarsDirectory() {
    if (!Files.exists(rootLocation)) {
      try {
        Files.createDirectory(rootLocation);
      } catch (IOException e) {
        throw new FailedDirectoryCreatingException(rootLocation.toString());
      }
    }
  }

  private boolean checkContentType(String contentType) {
    List<String> supportedContentTypes = Arrays.asList("jpeg/png", "image/png", "image/jpeg");
    return supportedContentTypes.contains(contentType);
  }
}
