package guru.qa.niffler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InvitationsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Sql(scripts = "/sendInvitationShouldWork.sql")
  @Test
  void sendInvitationShouldWork() throws Exception {
    mockMvc.perform(post("/internal/invitations/send")
            .param("username", "alice")
            .param("targetUsername", "bob")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("bob"))
        .andExpect(jsonPath("$.friendshipStatus").value("INVITE_SENT"));
  }

  @Sql(scripts = "/acceptInvitationShouldWork.sql")
  @Test
  void acceptInvitationShouldUpdateStatus() throws Exception {
    mockMvc.perform(post("/internal/invitations/accept")
            .param("username", "bob")
            .param("targetUsername", "alice"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("alice"))
        .andExpect(jsonPath("$.friendshipStatus").value("FRIEND"));
  }

  @Sql(scripts = "/declineInvitationShouldWork.sql")
  @Test
  void declineInvitationShouldUpdateStatus() throws Exception {
    mockMvc.perform(post("/internal/invitations/decline")
            .param("username", "bob")
            .param("targetUsername", "alice"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("alice"))
        .andExpect(jsonPath("$.friendshipStatus").doesNotExist());
  }
}