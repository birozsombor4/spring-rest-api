package com.birozsombor4.springrestapitemplate.avatar;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.birozsombor4.springrestapitemplate.models.daos.User;
import com.birozsombor4.springrestapitemplate.respositories.UserRepository;
import com.birozsombor4.springrestapitemplate.security.UserDetailsImpl;
import com.birozsombor4.springrestapitemplate.utils.JwtUtil;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql(value = {"/db/test/insert_fakeUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AvatarEndpointTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private JwtUtil jwtUtil;
  @Autowired
  private AvatarConfig avatarConfig;
  @Autowired
  private AvatarService avatarService;
  private String jwt;

  @Before
  public void before() {
    UserDetailsImpl fakeUserDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("fakeUser");
    jwt = jwtUtil.generateToken(fakeUserDetails);
  }

  @After
  public void tearDown() {
    avatarService.deleteAllCustomAvatar();
  }

  @Test
  public void uploadAvatar_WhenUserUploadNewAvatar_ReturnValidResponseAndStoreAvatar() throws Exception {
    User user = userRepository.findById(1).get();
    Assert.assertEquals("default.png", user.getAvatar());
    Assert.assertEquals(1, Paths.get(avatarConfig.getLocation()).toFile().listFiles().length);

    MockMultipartFile fakeFile = new MockMultipartFile("image", "image.png",
        String.valueOf(MediaType.IMAGE_PNG), "content".getBytes());

    mockMvc.perform(multipart("/avatar/1")
        .file(fakeFile)
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ok")))
        .andExpect(jsonPath("$.message", is("Avatar has updated for user: fakeUser")));

    User updatedUser = userRepository.findById(1).get();
    Assert.assertEquals("1.png", updatedUser.getAvatar());
    Assert.assertEquals(2, Paths.get(avatarConfig.getLocation()).toFile().listFiles().length);
  }

  @Test
  public void uploadAvatar_WhenUserUploadNotSupportedFile_ReturnValidStatusAndMessage() throws Exception {
    MockMultipartFile fakeFile = new MockMultipartFile("image", "plain_text.txt",
        "text/plain", "content".getBytes());

    mockMvc.perform(multipart("/avatar/1")
        .file(fakeFile)
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Unsupported Content-Type: text/plain")));
  }

  @Test
  public void uploadAvatar_WhenFilenameDoesntHaveExtension_ReturnValidStatusAndMessage() throws Exception {
    MockMultipartFile fakeFile = new MockMultipartFile("image", "imagepng",
        String.valueOf(MediaType.IMAGE_PNG), "content".getBytes());

    mockMvc.perform(multipart("/avatar/1")
        .file(fakeFile)
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Unsupported filename: Filename should has extension.")));
  }

  @Test
  @Sql(value = {"/db/test/insert_fakeUser.sql", "/db/test/insert_anotherFakeUser.sql"},
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(value = {"/db/test/clear_allTable.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
  public void uploadAvatar_WhenUserTriesToChangeAnotherUsersAvatar_ReturnValidStatusAndMessage() throws Exception {
    MockMultipartFile fakeFile = new MockMultipartFile("image", "imagepng",
        String.valueOf(MediaType.IMAGE_PNG), "content".getBytes());

    mockMvc.perform(multipart("/avatar/2")
        .file(fakeFile)
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message",
            is("User id: 2 doesn't belongs to user: fakeUser")));
  }

  @Test
  public void getAvatar_WhenUserDoesntExist_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(get("/avatar/999")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("User does not exist with given id/name: 999")));
  }

  @Test
  public void getAvatar_WhenImageDoesntExist_ReturnValidStatusAndMessage() throws Exception {
    User user = userRepository.findById(1).get();
    user.setAvatar("notExisting");
    userRepository.save(user);

    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Something went wrong with file loading: notExisting")));
  }

  @Test
  public void getAvatar_WhenUserAndImageExist_ReturnValidStatusAndMessage() throws Exception {
    mockMvc.perform(get("/avatar/1")
        .header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk())
        .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "filename=default.png"))
        .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, "image/png"))
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }
}