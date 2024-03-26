//package com.example.demo.service.impl;
//
//import com.example.demo.service.OAuth2Service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.security.SecureRandom;
//import java.util.Base64;
//
//@Service
//public class OAuth2ServiceImpl implements OAuth2Service {
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId = "1090063756103-f59en6qkrt6hvjeecgsl2c1n6ge08eoj.apps.googleusercontent.com";
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//    private String redirectUri = "http://localhost:8080/api/oauth2/callback/google-calendar";
//
//    @Value("${spring.security.oauth2.client.registration.google.scope}")
//    String scope = "https://www.googleapis.com/auth/calendar";
//
//    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
//    String authorizationUri = "https://accounts.google.com/o/oauth2/v2/auth";
//
//    @Override
//    public String buildAuthorizationUri() {
//        String state = generateRandomState();
//
//        return UriComponentsBuilder.fromUriString(authorizationUri)
//                .queryParam("client_id", clientId)
//                .queryParam("response_type", "code")
//                .queryParam("scope", scope)
//                .queryParam("redirect_uri", redirectUri)
//                .queryParam("state", state)
//                .build().toUriString();
//    }
//
//    @Override
//    public OAuth2AccessToken processAuthorizationCode(String code, String state) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("code", code);
//        params.add("grant_type", "authorization_code");
//        params.add("redirect_uri", redirectUri);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//        ResponseEntity<OAuth2AccessToken> response = restTemplate.postForEntity(
//                "https://oauth2.googleapis.com/token", request, OAuth2AccessToken.class);
//
//        return response.getBody();
//    }
//
//    @Override
//    public String generateRandomState() {
//        SecureRandom random = new SecureRandom();
//        byte[] bytes = new byte[24];
//        random.nextBytes(bytes);
//        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
//    }
//}
