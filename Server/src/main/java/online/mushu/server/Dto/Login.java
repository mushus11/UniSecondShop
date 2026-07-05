package online.mushu.server.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * =======
 * =======
 */
@Data
@AllArgsConstructor
public class Login {
    private int id;
    private String password;
}
