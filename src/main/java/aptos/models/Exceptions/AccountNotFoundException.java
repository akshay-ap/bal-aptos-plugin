package aptos.models.Exceptions;

public class AccountNotFoundException extends AptosClientException {

    public AccountNotFoundException(ErrorResponse response) {
        super(response);
    }
}
