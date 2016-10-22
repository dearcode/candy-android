package net.dearcode.candy.controller.command;

/**
 * Created by lujinfei on 2016/10/20.
 */

public class DoCandyWork {
    public enum TYPE {
        LOGIN, SENDMSG, ADDFRIEND
    }

    private TYPE type;
    private long id;
    private String param1;
    private String param2;
}
