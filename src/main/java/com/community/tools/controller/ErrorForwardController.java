package com.community.tools.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorForwardController implements ErrorController {

  private static final String PATH = "/error";

  @RequestMapping(value = PATH)
  public String forwardError(){
    return "forward:/index.html";
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }
}
