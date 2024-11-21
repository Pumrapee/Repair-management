package pumrapee.repairmanagementapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "Username is required")
    @NotNull(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    private String password;
}
