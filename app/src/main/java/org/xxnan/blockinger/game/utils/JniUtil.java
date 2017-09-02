package org.xxnan.blockinger.game.utils;

/**
 * Created by vanke on 2017/9/1.
 */

public class JniUtil {
    //加载so库
    static {
        System.loadLibrary("YouMiId");
    }

    //native方法
    public static native String getIdFromJni();
    public static native String getSecretFromJni();
}
