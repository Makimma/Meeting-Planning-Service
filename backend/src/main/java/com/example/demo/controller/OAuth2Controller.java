//package com.example.demo.controller;
//
//import com.example.demo.service.OAuth2Service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
//
//@RestController
//@RequestMapping("/api/oauth2")
//public class OAuth2Controller {
//
//    private final OAuth2Service oAuth2Service;
//
//    @Autowired
//    public OAuth2Controller(OAuth2Service oAuth2Service) {
//        this.oAuth2Service = oAuth2Service;
//    }
//
//    @GetMapping("/connect/google-calendar")
//    public ModelAndView connectToGoogleCalendar() {
//        return new ModelAndView("redirect:" + oAuth2Service.buildAuthorizationUri());
//    }
//
//    @GetMapping("/callback/google-calendar")
//    public ModelAndView handleGoogleCallback(@RequestParam String code, @RequestParam String state) {
//        oAuth2Service.processAuthorizationCode(code, state);
//        return new ModelAndView("redirect:/user/profile");
//    }
//}