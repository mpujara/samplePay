package controllers;

import play.mvc.Controller;

/**
 * Homepage Controller
 * 
 * @author mpujara
 *
 */
public class Application extends Controller {

    /**
     * render index.html 
     */
    public static void index() {
        render();
    }

}