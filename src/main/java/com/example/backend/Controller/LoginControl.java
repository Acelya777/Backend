package com.example.backend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginControl {
 /* @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView Login() {

        return new ModelAndView("login");
    }*/

    @RequestMapping(path = {"/basicauth"})
    @ResponseBody
    public AuthenticationBean basicauth() {
        return new AuthenticationBean("You are authenticated");
    }
}