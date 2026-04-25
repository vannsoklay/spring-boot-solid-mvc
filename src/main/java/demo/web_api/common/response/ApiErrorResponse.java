package demo.web_api.common.response;

import lombok.*;
import java.time.Instant;

@Data
@Builder
public class ApiErrorResponse {

    private Instant timestamp;
    private int status;
    private String errorCode;
    private String message;
    private String path;
}