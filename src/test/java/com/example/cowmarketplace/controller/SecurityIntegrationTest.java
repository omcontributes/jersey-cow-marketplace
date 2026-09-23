package com.example.cowmarketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void farmerOnlyEndpoint_withoutToken_returns401Or403() throws Exception {
        mockMvc.perform(post("/api/farmers/cows"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void publicCowListEndpoint_withoutToken_returns200() throws Exception {
        mockMvc.perform(get("/api/cows"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidJwtToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/farmers/cows")
                        .header("Authorization", "Bearer this.is.not.a.valid.jwt"))
                .andExpect(status().is4xxClientError());
    }
}