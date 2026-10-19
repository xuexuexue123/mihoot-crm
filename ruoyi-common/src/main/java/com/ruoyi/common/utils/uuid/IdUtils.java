package com.ruoyi.common.utils.uuid;

import java.util.Random;

/**
 * ID生成器工具类
 * 
 * @author ruoyi
 */
public class IdUtils
{

    public static int randomDIGIT() {
        Random ne=new Random();//实例化一个random的对象ne

        int x=ne.nextInt(9999-1000+1)+1000;//为变量赋随机值1000-9999

        return x;
    }
    /**
     * 获取随机UUID
     * 
     * @return 随机UUID
     */
    public static String randomUUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 随机UUID
     */
    public static String fastUUID()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID()
    {
        return UUID.fastUUID().toString(true);
    }
}
