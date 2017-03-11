package org.monster.framework.tes.common.utils.number;

import java.util.UUID;

/**
 * Project :       Monster-Tes-FrameWork
 * Author:         XIE-HONGFEI
 * Company:        hongfei tld.
 * Created Date:   2017/2/22 0022
 * Copyright @ 2017 Company hongfei tld. – Confidential and Proprietary
 * <p/>
 * History:
 * -----------------------------------------------------------------------
 *        Date       |       Author        |   Change Description
 * 2017/02/22 0022   |       谢洪飞        |   初版做成
 */

public class UUIDUtils {


    /**
     *   随机获取uid
     *
     * @return
     */
    public static String UID(){

        return UUID.randomUUID().toString();

    }

    /**
     *  获取指定字符串uuid
     * Static factory to retrieve a type 3 (name based) {@code UUID} based on
     * the specified string.
     * @param source
     * @return
     */
    public static String UID( final String source){
        return (source == null || "".equals(source)) ? null : UUID.nameUUIDFromBytes(source.getBytes()).toString();
    }


    public static void main(String ... args){

        System.out.println(UID());

        System.out.println(UID("default_user"));
    }

}
