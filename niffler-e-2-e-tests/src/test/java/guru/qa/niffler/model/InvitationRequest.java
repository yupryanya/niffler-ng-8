package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InvitationRequest(@JsonProperty("username") String username) {}
