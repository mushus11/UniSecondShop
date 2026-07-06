package online.mushu.server.Vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * =======
 * =======
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseInfVo {
    private String id;
    private int userId;
    private int type;
    private String goodName;
    private String text;
    private Timestamp purchaseTime;
    private double price;
}
