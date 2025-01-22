package br.com.hadryan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DownloadException extends ResponseStatusException {
    public DownloadException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
