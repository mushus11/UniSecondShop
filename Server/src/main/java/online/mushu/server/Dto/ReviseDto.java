package online.mushu.server.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======
 * =======
 */
@Data
@NoArgsConstructor
public class ReviseDto {

    private int id;
    private String name;
    private String college;
    private String grade;
    private String telephone;
    private String profile;
    private boolean certified;

}
