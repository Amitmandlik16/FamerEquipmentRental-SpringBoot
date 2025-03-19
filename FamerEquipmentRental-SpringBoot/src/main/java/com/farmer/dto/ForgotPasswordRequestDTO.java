package com.farmer.dto;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;

import com.farmer.entity.FileEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDTO {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	private String username;

}
