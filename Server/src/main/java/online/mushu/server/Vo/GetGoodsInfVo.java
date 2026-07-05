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
public class GetGoodsInfVo {
    private String id;
    private int userId;
    private String name;
    private int type;
    private double price;
    private String text;
    private boolean state;
}
