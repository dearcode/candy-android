package net.dearcode.candy.model;

/**
 *  * Created by c-wind on 2016/9/21 18:34
 *  * mail：root@codecn.org
 *  
 */
public class Group {
    // 组的图标
    private String img;
    private String name;
    private String date;
    private int count;

    public Group(String img, String name, String date, int count) {
        this.img = img;
        this.name = name;
        this.date = date;
        this.count = count;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }
}
