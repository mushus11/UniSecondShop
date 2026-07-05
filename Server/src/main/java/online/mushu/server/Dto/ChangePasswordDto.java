package online.mushu.server.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======
 * =======
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
    private int id;
    private String newPassword;
}
