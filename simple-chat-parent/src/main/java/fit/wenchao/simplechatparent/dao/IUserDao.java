package fit.wenchao.simplechatparent.dao;


import fit.wenchao.simplechatparent.model.UserPO;

public interface IUserDao {

    UserPO getUserByUsernameAndPwd(String username, String password);

    UserPO getUserByUsername(String username);

}
