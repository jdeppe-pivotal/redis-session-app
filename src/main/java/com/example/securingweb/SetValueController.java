package com.example.securingweb;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SetValueController {

  @RequestMapping(value = "/setValue", method = RequestMethod.POST)
  public String setValue(@RequestParam(name = "key", required = false) String key,
                         @RequestParam(name = "value", required = false) String value, HttpServletRequest request) {
    if (!ObjectUtils.isEmpty(key) && !ObjectUtils.isEmpty(value)) {
      request.getSession().setAttribute(key, value);
    }
    return "hello";
  }
}
