package aptos.models.Exceptions;

public class AptosSequenceNumberTooOldException extends AptosClientException {

    public AptosSequenceNumberTooOldException(ErrorResponse response) {
        super(response);
    }
}
