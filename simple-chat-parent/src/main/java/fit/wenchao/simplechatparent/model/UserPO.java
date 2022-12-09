package fit.wenchao.simplechatparent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPO {
    private String uid;
    private Long id;
    private String username;
    private String password;

    public static final UserPO NULL = new UserPO();




}
