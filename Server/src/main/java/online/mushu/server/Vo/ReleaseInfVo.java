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
public class ReleaseInfVo {
    public String id;
    private String GoodsID;
    private Timestamp upLoadTime;
    private Timestamp downLoadTime;
    private int state;
    private boolean hurryMark;
    private boolean topMark;
}
