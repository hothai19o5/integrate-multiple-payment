package com.javabuilder.stripe.exception;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse implements Serializable {
	private int code;
	private String error;
	private String message;
	private Date timestamp;
	private String path;
}
