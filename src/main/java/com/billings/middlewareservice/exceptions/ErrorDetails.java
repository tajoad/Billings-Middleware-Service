package com.billings.middlewareservice.exceptions;


import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorDetails {
    private int statusCode;
    private boolean status;
    private String message;
}
