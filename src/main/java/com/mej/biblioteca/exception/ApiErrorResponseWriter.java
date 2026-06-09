package com.mej.biblioteca.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ApiErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletRequest request, HttpServletResponse response, ApiException exception) throws IOException {
        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(java.time.ZoneId.of("UTC")),
                exception.getStatus().value(),
                exception.getErro(),
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        response.setStatus(exception.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
