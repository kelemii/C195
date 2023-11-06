package DAO;

import Model.User;

public interface UserDao {
    User findUserByUsernameAndPassword(String username, String password);
}
