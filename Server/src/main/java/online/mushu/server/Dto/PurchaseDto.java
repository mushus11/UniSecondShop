package online.mushu.server.Dto;

import lombok.AllArgsConstructor;
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
public class PurchaseDto {
    private int UserID;
    private String goodsName;
    private int type;
    private String text;
    private double price;
    private Timestamp purchaseTime;
}
