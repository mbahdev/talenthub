
package talenthub.talenthub.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import talenthub.talenthub.user.exception.EmailAlreadyExistsException;
import talenthub.talenthub.user.exception.UserNotFoundException;
import talenthub.talenthub.user.exception.ClientNotFoundException;
import talenthub.talenthub.user.exception.FreelanceNotFoundException;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.application.exception.ApplicationNotFoundException;
import talenthub.talenthub.application.exception.ApplicationNotPendingException;
import talenthub.talenthub.application.exception.ApplicationAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Ressources introuvables
    @ExceptionHandler({
            UserNotFoundException.class,
            ClientNotFoundException.class,
            FreelanceNotFoundException.class,
            MissionNotFoundException.class,
            ApplicationNotFoundException.class
    })
    public ResponseEntity<String> handleNotFound(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    // 409 - Conflits métier
    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            ApplicationAlreadyExistsException.class,
            ApplicationNotPendingException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<String> handleConflict(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    // 403 - Accès refusé (exception personnalisée)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    // 403 - Accès refusé (Spring Security)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleSpringAccessDenied(
            org.springframework.security.access.AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    // 400 - Validation Jakarta
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    // 400 - Arguments invalides
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(
            IllegalArgumentException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
