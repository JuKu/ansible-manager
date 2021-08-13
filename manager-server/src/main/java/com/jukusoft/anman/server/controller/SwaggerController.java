package com.jukusoft.anman.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * the spring Controller for swagger documentation which redirects the url.
 *
 * @author Justin Kuenzel
 */
@Controller
public class SwaggerController {

    /**
     * redirect /swagger to the swagger controller.
     *
     * @return redirect url
     */
    @GetMapping("/swagger")
    public String redirectToSwaggerUI() {
        return "redirect:/swagger-ui.html";
    }

}