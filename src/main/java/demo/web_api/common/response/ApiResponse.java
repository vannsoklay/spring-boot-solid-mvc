package demo.web_api.common.response;

import lombok.*;
import java.time.Instant;

@Data
@Builder
public class ApiResponse<T> {

    private Instant timestamp;
    private boolean success;
    private int status;
    private String message;
    private T data;
    private String path;
}