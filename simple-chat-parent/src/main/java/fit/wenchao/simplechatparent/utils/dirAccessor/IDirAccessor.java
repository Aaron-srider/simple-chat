package fit.wenchao.simplechatparent.utils.dirAccessor;

import java.io.IOException;

public interface IDirAccessor {
    void cd(String target) throws IOException;

    String pwd();
}
