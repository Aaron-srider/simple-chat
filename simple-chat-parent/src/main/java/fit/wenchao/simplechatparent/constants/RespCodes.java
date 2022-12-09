package fit.wenchao.simplechatparent.constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespCodes {
    String code;
    String desc;
    public static final RespCodes SUCCESS = new RespCodes("200", "success");
    public static final RespCodes LOGIN_FAIL = new RespCodes("100001", "login fail");

    public static final RespCodes TARGET_USER_NOT_ONLINE = new RespCodes("100002", "target user not exist");

}
