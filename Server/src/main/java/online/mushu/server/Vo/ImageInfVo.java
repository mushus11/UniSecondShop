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
public class ImageInfVo {
    private String text;
    private boolean voucher;
    private String title;
}
