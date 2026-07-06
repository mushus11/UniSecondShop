package online.mushu.server.Vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======
 * =======
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfVo {
    private int code;
    private int id;
    private String name;
    private String college;
    private String grade;
    private String telephone;
    private String profile;
    private boolean certified;
    private String image;
    private String access;
}
