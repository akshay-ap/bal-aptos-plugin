package aptos.models.Exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    @JsonProperty("message")
    public String message;

    @JsonProperty("error_code")
    public ErrorCode errorCode;

    @JsonProperty("vm_error_code")
    public int vmErrorCode;

}
