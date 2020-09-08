package com.birozsombor4.springrestapitemplate.avatar;

import com.birozsombor4.springrestapitemplate.exceptions.FailedFileDeletingException;
import com.birozsombor4.springrestapitemplate.exceptions.FailedFileLoadingException;
import com.birozsombor4.springrestapitemplate.exceptions.UnsupportedContentTypeException;
import com.birozsombor4.springrestapitemplate.exceptions.UnsupportedFileNameException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class AvatarServiceTest {

  private AvatarConfig avatarConfig;
  private AvatarService avatarService;

  @Before
  public void before() {
    avatarConfig = new AvatarConfig("./src/test/java/resources/avatars");
    avatarService = new AvatarServiceImpl(avatarConfig);
  }

  @After
  public void tearDown() {
    avatarService.deleteAllCustomAvatar();
  }

  @Test(expected = UnsupportedContentTypeException.class)
  public void renameAndStore_WhenContentTypeIsNotSupported_ThrowException() throws IOException {
    MultipartFile fakeFile = new MockMultipartFile("plain_text", "plain_text.txt", String.valueOf(MediaType.TEXT_PLAIN),
        "content".getBytes());

    avatarService.renameAndStore(fakeFile, 1);
  }

  @Test(expected = UnsupportedFileNameException.class)
  public void renameAndStore_WhenFileNameNotContainsDotCharacter_ThrowException() throws IOException {
    MultipartFile fakeFile = new MockMultipartFile("image", "imagepng", String.valueOf(MediaType.IMAGE_PNG),
        "content".getBytes());

    avatarService.renameAndStore(fakeFile, 1);
  }

  @Test
  public void renameAndStore_WhenFileNameIsValid_ReturnsNewFilenameAndStoreTheFile() throws IOException {
    MultipartFile fakeFile = new MockMultipartFile("image", "image.png", String.valueOf(MediaType.IMAGE_PNG),
        "content".getBytes());

    String result = avatarService.renameAndStore(fakeFile, 1);

    Assert.assertEquals("1.png", result);
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).resolve(result).toFile().exists());
  }

  @Test
  public void renameAndStore_WhenUserHasAlreadyUpdated_RenameAndReplaceExistingImage() throws IOException {
    MultipartFile fakeFile = new MockMultipartFile("image", "image.png", String.valueOf(MediaType.IMAGE_PNG),
        "content".getBytes());

    String result = avatarService.renameAndStore(fakeFile, 1);
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).resolve(result).toFile().exists());
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).toFile().listFiles().length == 2);

    MultipartFile newFakeFile = new MockMultipartFile("anotherImage", "anotherImage.png",
        String.valueOf(MediaType.IMAGE_PNG), "content".getBytes());

    String secondImageResult = avatarService.renameAndStore(newFakeFile, 1);
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).resolve(secondImageResult).toFile().exists());
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).toFile().listFiles().length == 2);
  }

  @Test(expected = FailedFileLoadingException.class)
  public void loadAvatarAsResource_WhenFileDoesNotExist_ThrowException() throws MalformedURLException {
    avatarService.loadAvatarAsResource("notExisting");
  }

  @Test
  public void loadAvatarAsResource_WhenFileExist_ReturnFile() throws MalformedURLException {
    Resource resource = avatarService.loadAvatarAsResource("default.png");

    Assert.assertTrue(resource.exists());
    Assert.assertEquals("default.png", resource.getFilename());
  }

  @Test
  public void deleteAvatar_WithExistingFile_DeleteSelectedAvatar() throws IOException {
    MultipartFile fakeFile = new MockMultipartFile("image", "image.png", String.valueOf(MediaType.IMAGE_PNG),
        "content".getBytes());
    String storedFileName = avatarService.renameAndStore(fakeFile, 1);
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).resolve(storedFileName).toFile().exists());

    avatarService.deleteAvatar(storedFileName);
    Assert.assertTrue(!Paths.get(avatarConfig.getLocation()).resolve(storedFileName).toFile().exists());
  }

  @Test(expected = FailedFileDeletingException.class)
  public void deleteAvatar_WithNotExistingFile_ThrowException() throws IOException {
    avatarService.deleteAvatar("notExisting");
  }

  @Test
  public void deleteAllCustomAvatar_DeleteAllCustomAvatarAndDoesntDeleteDefault() throws IOException {
    MultipartFile fakeFile1 = new MockMultipartFile("image1", "image1.png", String.valueOf(MediaType.IMAGE_PNG),
        "content".getBytes());
    MultipartFile fakeFile2 = new MockMultipartFile("image2", "image2.png", String.valueOf(MediaType.IMAGE_PNG),
        "content".getBytes());
    avatarService.renameAndStore(fakeFile1, 1);
    avatarService.renameAndStore(fakeFile2, 2);
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).toFile().listFiles().length == 3);

    avatarService.deleteAllCustomAvatar();

    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).toFile().listFiles().length == 1);
    Assert.assertTrue(Paths.get(avatarConfig.getLocation()).resolve("default.png").toFile().exists());
  }

  @Test
  public void getAvatarContentType_WithJpg_ReturnsValidContentType() {
    String fakeFileName = "image.jpg";

    String result = avatarService.getAvatarContentType(fakeFileName);

    Assert.assertEquals("jpeg/png", result);
  }

  @Test
  public void getAvatarContentType_WithJpeg_ReturnsValidContentType() {
    String fakeFileName = "image.jpeg";

    String result = avatarService.getAvatarContentType(fakeFileName);

    Assert.assertEquals("jpeg/png", result);
  }

  @Test
  public void getAvatarContentType_WithJPG_ReturnsValidContentType() {
    String fakeFileName = "image.JPG";

    String result = avatarService.getAvatarContentType(fakeFileName);

    Assert.assertEquals("image/jpeg", result);
  }

  @Test
  public void getAvatarContentType_WithJPEG_ReturnsValidContentType() {
    String fakeFileName = "image.JPEG";

    String result = avatarService.getAvatarContentType(fakeFileName);

    Assert.assertEquals("image/jpeg", result);
  }


  @Test
  public void getAvatarContentType_WithPng_ReturnsValidContentType() {
    String fakeFileName = "image.png";

    String result = avatarService.getAvatarContentType(fakeFileName);

    Assert.assertEquals("image/png", result);
  }

  @Test(expected = UnsupportedContentTypeException.class)
  public void getAvatarContentType_WithNotSupportedContentType_ThrowException() {
    String fakeFileName = "image.txt";

    avatarService.getAvatarContentType(fakeFileName);
  }
}
