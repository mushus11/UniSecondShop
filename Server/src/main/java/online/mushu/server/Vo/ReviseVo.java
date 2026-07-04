package online.mushu.server.Vo;

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
public class ReviseVo {

    private int id;
    private String college;
    private String grade;
    private String telephone;
    private String profile;
    private boolean certified;

}
