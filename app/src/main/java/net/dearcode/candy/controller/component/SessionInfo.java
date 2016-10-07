package net.dearcode.candy.controller.component;

import net.dearcode.candy.controller.CustomeApplication;
import net.dearcode.candy.model.Session;
import net.dearcode.candy.model.User;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/30 15:32
 *  * mail：root@codecn.org
 *  
 */
public class SessionInfo {

    public static ArrayList<Session> loadSessionList() {
        ArrayList<Session> ss = CustomeApplication.db.loadSession();
        for (int i = 0; i < ss.size(); i++) {
            Session s = ss.get(i);
            if (s.isGroup()) {
                //TODO 查组信息，头像什么的
            } else {
                User user = UserInfo.getUserInfo(s.getUser());
                s.setTitle(user.getNickName());
                s.setAvatar(user.getAvatar());
            }
            ss.set(i, s);
        }
        return ss;
    }
}
