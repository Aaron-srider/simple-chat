package fit.wenchao.simplechatparent.model.business;

import fit.wenchao.simplechatparent.proto.IMessageData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq implements IMessageData, Serializable {

    private String username;
    private String password;

}
