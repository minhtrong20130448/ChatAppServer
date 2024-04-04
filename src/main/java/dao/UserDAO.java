package dao;

import dao.bean.UserBean;
import org.jdbi.v3.core.Jdbi;

public class UserDAO extends BaseDAO {
    public static final String TABLE_NAME = "users";

    public static UserBean getUserLogin(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return null;
        }
        return jdbi.withHandle(h -> h.createQuery("select id,username,password,player_name,gender,sponsor,email,phone,active,tree,relogin_token,agency_level,is_bot from " + TABLE_NAME + " where username = :username")
                .bind("username", username)
                .mapToBean(UserBean.class).stream().findFirst().orElse(null));
    }
    public static UserBean getUser(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return null;
        }
        return jdbi.withHandle(h -> h.createQuery("select id,username,password,player_name,gender,sponsor,email,phone,active,tree,relogin_token,agency_level,is_bot from " + TABLE_NAME + " where username = :username")
                .bind("username", username)
                .mapToBean(UserBean.class).stream().findFirst().orElse(null));
    }

    public static void main(String[] args) {
        UserBean userBean = getUserLogin("minhtrong");
        System.out.println(userBean);
    }
}
