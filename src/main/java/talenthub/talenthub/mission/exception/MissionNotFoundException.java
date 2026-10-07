package talenthub.talenthub.mission.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MissionNotFoundException extends RuntimeException {

    public MissionNotFoundException(Long id) {
        super("Mission not found with id: " + id);
    }
}
