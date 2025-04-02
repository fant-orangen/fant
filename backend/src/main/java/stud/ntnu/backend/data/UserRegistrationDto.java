package stud.ntnu.backend.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank
  @Size(max = 50, message = "Display name must be less than 50 characters")
  private String displayName;  // Optional

  @Size(max = 50, message = "First name must be less than 50 characters")
  private String firstName;

  @Size(max = 50, message = "Last name must be less than 50 characters")
  private String lastName;

  @Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$",
      message = "Phone number must be in international format (+XX XXXX XXX XXX)")
  private String phone;
}
