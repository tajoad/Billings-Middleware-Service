package com.billings.middlewareservice.dtos.response;


import lombok.*;

import java.time.Instant;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Data
public class ApiResponseDto<T> {

    private boolean success;
    private String message;
    private T data;
    private Instant timestamp;;

    public ApiResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    // Quick helper methods for cleaner Controller code
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return new ApiResponseDto<>(true, message, data);
    }

    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, message, null);
    }
}
