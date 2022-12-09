package fit.wenchao.simplechatparent.utils.cmd;

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
public class CmdOptionAttr {
    String option;
    String optionFullName;
    boolean hasValue;
    String desc;
    boolean required;
}




