package talenthub.talenthub.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FreelanceNotFoundException extends RuntimeException {

    public FreelanceNotFoundException(Long id) {
        super("Freelance not found with id: " + id);
    }
}