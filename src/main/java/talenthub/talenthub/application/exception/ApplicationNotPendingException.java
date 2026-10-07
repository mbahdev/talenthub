package talenthub.talenthub.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ApplicationNotPendingException extends RuntimeException {

    public ApplicationNotPendingException(Long id) {
        super("Application " + id + " cannot be withdrawn because it is not pending");
    }
}