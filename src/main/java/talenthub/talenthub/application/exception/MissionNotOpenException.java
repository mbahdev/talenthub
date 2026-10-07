package talenthub.talenthub.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MissionNotOpenException extends RuntimeException {

    public MissionNotOpenException() {
        super("Mission is not open for applications");
    }
}