package my.refuge.controllers.auth;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @GetMapping
    public String handleError(HttpServletRequest request) {
        String errorPage = "error"; // Page d'erreur par défaut

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status!= null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // Gestion de l'erreur 404
                errorPage = "error/error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // Gestion de l'erreur 500
                errorPage = "error/error-500";
            }
        }

        return errorPage;
    }
}
