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
public class GoodDto {
    private int userId;
    private String name;
    private int type;
    private double price;
    private String text;
    private boolean state;
}
