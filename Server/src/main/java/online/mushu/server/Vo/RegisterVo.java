package online.mushu.server.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======
 * =======
 */
@Data
@NoArgsConstructor
public class RegisterVo {

    private int id;
    private int code;

    public RegisterVo(int id, int code) {
        setId(id);
        setCode(code);
    }

    public static RegisterVo Success(int id) {
        return new RegisterVo(id, 200);
    }

}
