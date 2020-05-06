package javaBase;

/**
 * 代理类
 */
public class UserDAOProxy implements IUserDAO{

    private UserDAO userDAO;

    public UserDAOProxy(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void save() {
        System.out.println("开始事务");
        userDAO.save();
        System.out.println("结束事务");
    }
}
