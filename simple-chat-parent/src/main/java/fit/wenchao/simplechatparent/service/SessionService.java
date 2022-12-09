package fit.wenchao.simplechatparent.service;

import fit.wenchao.simplechatparent.dao.IUserDao;
import fit.wenchao.simplechatparent.model.UserPO;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService implements ISessionService {

    @Autowired
    private IUserDao userDao;

    private Map<Long, Channel> onlineUserMap = new ConcurrentHashMap<>();


    @Override
    public UserPO userOnline(String username) {

        // ensure that user exists
        UserPO userByUsername = userDao.getUserByUsername(username);
        if (userByUsername == UserPO.NULL) {
            throw new RuntimeException("User not exists: " + username);
        }

        Long id = userByUsername
                .getId();

        // ensure that user online
        for (Map.Entry<Long, Channel> entry : onlineUserMap.entrySet()) {
            if (id.equals(entry.getKey())) {
                return userByUsername;
            }
        }

        return UserPO.NULL;
    }

    @Override
    public Channel getOnlineChannel(String username) {
        UserPO userOnline = this.userOnline(username);
        if (userOnline == UserPO.NULL) {
            return null;
        }
        else {
            return onlineUserMap.get(userOnline.getId());
        }
    }

    @Override
    public void online(Long userId, Channel channel){
        onlineUserMap.put(userId, channel);
    }

}
