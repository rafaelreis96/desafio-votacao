package dev.rafaelreis.desafiovotacao.exception.handler;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class ErrorResponse {
    private int statusCode;
    private String description;
    private String target;
    private LocalDateTime timestamp;
    private List<ErrorMessage> errors;

    public ErrorResponse(int statusCode, String description, String target, List<ErrorMessage> errors) {
        this.statusCode = statusCode;
        this.description = description;
        this.target = target;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessage> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "statusCode=" + statusCode +
                ", description='" + description + '\'' +
                ", target='" + target + '\'' +
                ", timestamp=" + timestamp +
                ", errors=" + errors +
                '}';
    }
}