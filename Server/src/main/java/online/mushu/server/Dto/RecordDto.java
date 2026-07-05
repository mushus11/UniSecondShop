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
public class RecordDto {
    private int buyerID;
    private int sellerID;
    private String goodID;
    private String tradingLocation;
    private String note;
    private boolean state;
    private double price;
    private Timestamp transactionTime;
}
