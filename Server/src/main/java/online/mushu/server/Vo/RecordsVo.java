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
public class RecordsVo {
    private String id;
    private int buyerId;
    private int sellerId;
    private String goodID;
    private Timestamp transactionTime;
    private String tradingLocation;
    private String node;
    private double price;
    private boolean state;
}
