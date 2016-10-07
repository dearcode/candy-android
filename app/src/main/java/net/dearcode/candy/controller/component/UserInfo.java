package net.dearcode.candy.controller.component;

import android.util.Log;

import net.dearcode.candy.controller.CustomeApplication;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;

/**
 *  * Created by c-wind on 2016/9/30 13:23
 *  * mail：root@codecn.org
 *  
 */
public class UserInfo {

    public static User getUserInfo(final long id) {
        User user = CustomeApplication.db.loadUser(id);
        if (user != null) {
            return user;
        }
        ServiceResponse sr = new RPC() {
            public ServiceResponse getResponse() throws Exception {
                return CustomeApplication.getService().loadUserInfo(id);
            }
        }.Call();
        if (sr.hasError()) {
            Log.e("", "rpc load userInfo error:"+sr.getError());
            return null;
        }
        user = sr.getUser();

        CustomeApplication.db.saveUser(user.getID(), user.getName(), user.getNickName(), user.getAvatar());

        return user;
    }

}
