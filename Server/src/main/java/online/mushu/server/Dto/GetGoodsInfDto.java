package online.mushu.server.Dto;

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
public class GetGoodsInfDto {
    private int userID;
    private String goodID;
}
