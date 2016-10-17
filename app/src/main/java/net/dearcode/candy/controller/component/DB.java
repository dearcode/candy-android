package net.dearcode.candy.controller.component;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.dearcode.candy.model.ConversationListItem;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.Session;
import net.dearcode.candy.model.User;
import net.dearcode.candy.modelview.MessageBean;

import java.util.ArrayList;
import java.util.Collections;

/**
 *  * Created by c-wind on 2016/9/29 12:43
 *  * mail：root@codecn.org
 *  
 */
public class DB {
    private static SQLiteDatabase db;

    public boolean isFriend(long id) {
        Cursor c = db.rawQuery("SELECT id FROM friend where id= " + id + " limit 1", null);
        if (c.moveToNext()) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public void saveFriend(long id) {
        db.execSQL("replace into friend (id) values ( " + id + ")");
    }


    public User loadUser(long id) {
        User user = null;
        Cursor c = db.rawQuery("SELECT id, name,nickname,avatar FROM user_info where id = " + id, null);
        if (c.moveToNext()) {
            user = new User();
            user.setID(c.getLong(0));
            user.setName(c.getString(1));
            user.setNickName(c.getString(2));
            user.setAvatar(c.getBlob(3));
        }
        c.close();
        return user;
    }

    public void saveUser(long id, String name, String nickname, byte[] avatar) {
        db.execSQL("replace into user_info(id, name, nickname, avatar) values (?,?,?,?)", new Object[]{id, name, nickname, avatar});
    }

    public User loadAccount() {
        User user = null;
        Cursor c = db.rawQuery("SELECT id, user,nickname,password, avatar FROM account limit 1", null);
        if (c.moveToNext()) {
            user = new User();
            user.setID(c.getLong(0));
            user.setName(c.getString(1));
            user.setNickName(c.getString(2));
            user.setPassword(c.getString(3));
            user.setAvatar(c.getBlob(4));
            Log.e("", "ID:" + user.getID() + "user:" + user.getName() + " pass:" + user.getPassword());
        }
        c.close();
        return user;
    }

    public void saveAccount(long id, String user, String password) {
        db.execSQL("replace into account(id, user, password) values (?,?,?)", new Object[]{id, user, password});
    }

    public DB(Context ctx) {
        db = ctx.openOrCreateDatabase("candy.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS account(id INTEGER PRIMARY KEY, user TEXT, name TEXT, password TEXT, nickname TEXT, avatar BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS user_info (id INTEGER PRIMARY KEY, name TEXT, nickname TEXT, avatar BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS friend(id INTEGER PRIMARY KEY)");

        // 系统消息， 类型， 哪个群触发的，哪个用户触发的，具体消息内容，如：群A看用户B修改了群名为msg
        db.execSQL("CREATE TABLE IF NOT EXISTS system_message(id INTEGER PRIMARY KEY, event integer, relation integer, `group` integer, `from` integer, msg text)");

        // 群消息，哪个群，谁发的，是否@了其它人，具体消息
        db.execSQL("CREATE TABLE IF NOT EXISTS group_message(id INTEGER PRIMARY KEY, `group` integer, `from` integer, `to` integer, msg text)");
        db.execSQL("create index if not exists group_message_index on group_message(`group`)");

        // 聊天消息，跟谁聊天，谁发的（对方或者自己），具体消息内容
        db.execSQL("CREATE TABLE IF NOT EXISTS user_message(id INTEGER PRIMARY KEY, user integer, `from` integer, msg text)");
        db.execSQL("create index if not exists user_message_index on user_message(user)");

        // 会话从这加载 -- 即聊天列表
        db.execSQL("CREATE TABLE IF NOT EXISTS session (id varchar(30) PRIMARY KEY, type integer, last_time integer, msg text)");

    }

    public ArrayList<Session> loadSession() {
        ArrayList<Session> ss = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT id, is_group,last_time,msg from session order by last_time desc", null);
        while (c.moveToNext()) {
            long id = c.getLong(0);
            int isGroup = c.getInt(1);
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(c.getLong(2)));
            String msg = c.getString(3);
            if (isGroup == 1) {
                ss.add(new Session(id, 0, date, msg));
            } else {
                ss.add(new Session(0, id, date, msg));
            }
        }
        return ss;
    }

    public void saveSession(long id, boolean isGroup, String msg) {
        db.execSQL("replace into session(id, is_group, last_time, msg) values (?, ?, ?, ?)", new Object[]{id, isGroup ? 1 : 0, System.currentTimeMillis(), msg});
    }

    public ArrayList<Message> loadSystemMessage() {
        ArrayList<Message> msgs = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT id, event, relation, `group`, `from`, msg from system_message order by id desc limit 10", null);
        while (c.moveToNext()) {
            Message msg = new Message();
            msg.setId(c.getLong(0));
            msg.setEvent(c.getInt(1));
            msg.setRelation(c.getInt(2));
            msg.setGroup(c.getLong(3));
            msg.setFrom(c.getLong(4));
            msg.setMsg(c.getString(5));
            msgs.add(msg);
        }
        c.close();
        return msgs;
    }

    public ArrayList<Message> loadGroupMessage(long group) {
        ArrayList<Message> msgs = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT id, `from`, to, msg FROM group_message where `group` = ? order by id desc limit 10", new String[]{"" + group});
        while (c.moveToNext()) {
            Message msg = new Message();
            msg.setId(c.getLong(0));
            msg.setGroup(group);
            msg.setFrom(c.getLong(1));
            msg.setTo(c.getLong(2));
            msg.setMsg(c.getString(3));
            msgs.add(msg);
        }
        c.close();
        return msgs;
    }

    public void saveGroupMessage(long id, long group, long from, long to, String msg) {
        db.execSQL("insert into group_message(id, `group`, `from`, to, msg) values (?,?, ?, ?,?)", new Object[]{id, group, from, to, msg});
    }

    public void saveSystemMessage(long id, int event, int relation, long group, long from, String msg) {
        db.execSQL("insert into system_message(id, event, relation, `group`, `from`, msg) values (?,?, ?, ?, ?,?)", new Object[]{id, event, relation, group, from, msg});
    }

    /**
     * 获取所有聊天内容
     * @param id
     * @return
     */
    public ArrayList<Message> loadUserMessage(long id) {
        Log.i("****************" , "aaaa" + id);
        ArrayList<Message> msgs = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT id, `from`, msg FROM user_message where user = ? order by id desc limit 100", new String[]{"" + id});
        while (c.moveToNext()) {
            Message msg = new Message();
            msg.setId(c.getLong(0));
            msg.setFrom(c.getLong(1));
            msg.setMsg(c.getString(2));
            msgs.add(msg);
        }
        c.close();
        Collections.reverse(msgs);
        return msgs;
    }

    /**
     * 获取对话的条数
     * @param id
     * @return
     */
    public int getUserMessageCount(long id) {
        int ret = 0;
        Cursor c = db.rawQuery("SELECT count(1) FROM user_message where user = ? ", null);
        if (c.moveToNext()) {
            ret  = c.getInt(0);
        }
        c.close();
        return ret;
    }

    /**
     * 保存个人对话信息
     * @param id
     * @param user
     * @param from
     * @param msg
     */
    public void saveUserMessage(long id, long user, long from, String msg) {
        db.execSQL("insert into user_message(id, user, `from`, msg) values (?, ?, ?,?)", new Object[]{id, user, from, msg});
    }

    /**
     * 说话记录保存到对话列表
     * @param message
     */
    public void saveChatList(MessageBean message) {
        Log.i("****************", "dddddd" + message.getUser().getUserId());
        db.execSQL("replace into session(id, type, last_time, msg) values (?, ?, ?, ?)",
                new Object[]{message.getUser().getUserId(), "0", message.getTime(), message.getContent()});
    }

    /**
     * 获取好友列表
     * @return
     */
    public ArrayList<User> getFriends() {
        ArrayList<User> users = new ArrayList<User>();
        Cursor c = db.rawQuery("SELECT id , name , nickname , avatar FROM user_info where id in (select id from friend)", new String[]{});
        while (c.moveToNext()) {
            User user = new User();
            user.setID(c.getLong(0));
            user.setName(c.getString(1));
            user.setNickName(c.getString(2));
            users.add(user);
        }
        c.close();
        Collections.reverse(users);
        return users;
    }

    public ArrayList<ConversationListItem> getChatList() {
        ArrayList<ConversationListItem> chatList = new ArrayList<ConversationListItem>();
        Cursor c = db.rawQuery("SELECT user_info.id , user_info.name , user_info.nickname , user_info.avatar,"
                + " session.last_time, session.msg "
                + " FROM session, user_info where session.id=user_info.id order by - session.last_time ", new String[]{});
        while (c.moveToNext()) {
            ConversationListItem item = new ConversationListItem();
            item.setmContent(c.getString(5));
            item.setmDate(c.getLong(4));
            item.setmLastWord(c.getString(5));
            item.setmUnreadCount(0);
            item.setmUserId(c.getLong(0));
            item.setmUserName(c.getString(1));

            chatList.add(item);
        }
        c.close();
        //Collections.reverse(users);
        return chatList;
    }
}
