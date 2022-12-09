package fit.wenchao.simplechatparent.dao;

import fit.wenchao.simplechatparent.model.UserPO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserDao implements IUserDao {

    private static Map<Long, UserPO> usermap = new ConcurrentHashMap<>();

    static {
        usermap.put(1L, new UserPO("gakjsdf",1L, "wc", "123456"));
        usermap.put(2L, new UserPO("ngiuohe",2L, "cc", "123456"));
        usermap.put(3L, new UserPO("h9qhf",3L, "zs", "123456"));
        usermap.put(4L, new UserPO("nlouisegr9",4L, "ls", "123456"));
        usermap.put(5L, new UserPO("y73qhf",5L, "ww", "123456"));
    }


    @Override
    public  UserPO getUserByUsernameAndPwd(String username, String password) {

        for (Map.Entry<Long, UserPO> entry : usermap.entrySet()) {
            if (entry.getValue().getUsername().equals(username) && entry.getValue().getPassword().equals(password)) {
                UserPO userPO = new UserPO();
                BeanUtils.copyProperties(entry.getValue(), userPO);
                userPO.setPassword(null);
                return userPO;
            }
        }

        return UserPO.NULL;
    }

    @Override
    public  UserPO getUserByUsername(String username) {

        for (Map.Entry<Long, UserPO> entry : usermap.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                UserPO userPO = new UserPO();
                BeanUtils.copyProperties(entry.getValue(), userPO);
                userPO.setPassword(null);
                return userPO;
            }
        }

        return UserPO.NULL;
    }

}
