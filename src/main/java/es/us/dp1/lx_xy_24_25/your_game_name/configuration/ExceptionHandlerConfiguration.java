package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ExceptionHandlerConfiguration 
{
	@SuppressWarnings("unused")
	@Autowired
	private BasicErrorController errorController;

   @ExceptionHandler(Exception.class)
   public String defaultErrorHandler(HttpServletRequest request,  Exception ex)  {
        request.setAttribute("jakarta.servlet.error.request_uri", request.getPathInfo());
        request.setAttribute("jakarta.servlet.error.status_code", 400);
        request.setAttribute("exeption", ex);
        return "exception";
    }
}