
package talenthub.talenthub.user.exception;

public class FreelanceNotFoundException extends RuntimeException {

    public FreelanceNotFoundException(Long id) {
        super("Freelance not found with id: " + id);
    }
}
