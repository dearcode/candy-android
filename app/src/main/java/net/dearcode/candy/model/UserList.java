package net.dearcode.candy.model;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/27 13:12
 *  * mail：root@codecn.org
 *  
 */
public class UserList {
    public User[] Users;

    public UserList() {
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> list = new ArrayList<>();
        if (Users == null) {
            return list;
        }
        for (User u : Users) {
            list.add(u);
        }
        return list;
    }

    public void setUsers(User[] users) {
        Users = users;
    }

}
