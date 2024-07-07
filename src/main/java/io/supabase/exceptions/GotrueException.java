package io.supabase.exceptions;

import org.springframework.http.ResponseEntity;

import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Errors from the GoTrue server are wrapped by this exception
 */
public class GotrueException extends Exception {

    private ResponseEntity<?> response;
    private String content;
    private int statusCode;
    private FailureHint.Reason reason;

    /**
     * Something went wrong with Gotrue / Auth
     *
     * @param message Short description of the error source
     */
    public GotrueException(String message) {
        super(message);
    }

    /**
     * Something went wrong with Gotrue / Auth
     *
     * @param message       Short description of the error source
     * @param innerException The underlying exception
     */
    public GotrueException(String message, Exception innerException) {
        super(message, innerException);
    }

    /**
     * Something went wrong with Gotrue / Auth
     *
     * @param message Short description of the error source
     * @param reason  Best effort attempt to detect the reason for the failure
     */
    public GotrueException(String message, FailureHint.Reason reason) {
        super(message);
        this.reason = reason;
    }

    /**
     * Something went wrong with Gotrue / Auth
     *
     * @param message       Short description of the error source
     * @param reason        Assigned reason
     * @param innerException The underlying exception
     */
    public GotrueException(String message, FailureHint.Reason reason, Throwable innerException) {
        super(message, innerException);
        this.reason = reason;
    }

    /**
     * The HTTP response from the server
     */
    public Optional<ResponseEntity<?>> getResponse() {
        return Optional.ofNullable(response);
    }

    /**
     * Sets the HTTP response from the server
     */
    public void setResponse(ResponseEntity<?> response) {
        this.response = response;
    }

    /**
     * The content of the HTTP response from the server
     */
    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    /**
     * Sets the content of the HTTP response from the server
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * The HTTP status code from the server
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the HTTP status code from the server
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Adds the best-effort reason for the failure
     */
    public void addReason() {
        this.reason = FailureHint.detectReason(this);
        // System.out.println(content);
    }

    /**
     * Best guess at what caused the error from the server, see {@link FailureHint.Reason}
     */
    public FailureHint.Reason getReason() {
        return reason;
    }
}