package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface OAuthService {
    JsonNode exchangeCodeForTokens(String code);
    void refreshAccessToken();
}
