package io.supabase.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.ResponseEntity;

/**
 * A wrapper class from which all Responses derive.
 */
public class BaseResponse {

    /**
     * The HTTP response message.
     */
    @JsonIgnore
    private ResponseEntity<String> responseMessage;

    /**
     * The HTTP response content as a string.
     */
    @JsonIgnore
    private String content;

    // Constructor
    public BaseResponse(String content, ResponseEntity<String> responseMessage) {
        this.content = content;
        this.responseMessage = responseMessage;
    }

    // Getters and setters
    public ResponseEntity<String> getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(ResponseEntity<String> responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}