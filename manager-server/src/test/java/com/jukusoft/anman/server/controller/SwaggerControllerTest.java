package com.jukusoft.anman.server.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * test class for the swagger controller (redirect for swagger).
 *
 * @author Justin Kuenzel
 */
public class SwaggerControllerTest {

    /**
     * check, that the redirect for /swagger works.
     */
    @Test
    public void testRedirect() {
        SwaggerController controller = new SwaggerController();
        assertTrue(controller.redirectToSwaggerUI().startsWith("redirect:"));
        assertEquals("redirect:/swagger-ui.html", controller.redirectToSwaggerUI());
    }

}
