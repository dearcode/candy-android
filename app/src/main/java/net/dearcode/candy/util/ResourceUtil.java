package net.dearcode.candy.util;

import net.dearcode.candy.R;

import java.lang.reflect.Field;

/**
 * Created by lujinfei on 2016/10/21.
 */

public class ResourceUtil {
    public static int getDrawableId(String key) {
        try {
            String imgPath = key;
            Field field = R.drawable.class.getDeclaredField(imgPath);
            int resourceId = Integer.parseInt(field.get(null).toString());
            return resourceId;
        }catch(Exception e) {
            return 0;
		}
    }
}
