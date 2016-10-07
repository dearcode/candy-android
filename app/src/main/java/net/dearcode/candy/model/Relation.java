package net.dearcode.candy.model;

/**
 * Created by Tian on 2016/10/2.
 */

public enum Relation {
    DEL("没关系, 已删除", 0), ADD("添加", 1), CONFIRM("确认", 2), REFUSE("拒绝", 4);
    private String name;
    private int index;

    Relation(String name, int index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public String toString() {
        return this.index + "_" + this.name;
    }
}
