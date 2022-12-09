package fit.wenchao.simplechatparent.service;

import fit.wenchao.simplechatparent.model.UserPO;
import io.netty.channel.Channel;

public interface ISessionService {

    UserPO userOnline(String username);

    Channel getOnlineChannel(String username);

    void online(Long userId, Channel channel);
}
