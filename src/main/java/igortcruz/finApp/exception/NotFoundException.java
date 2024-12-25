package igortcruz.finApp.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Long id) {
        super("Não foi encontrado ID: " + id);
    }
}
