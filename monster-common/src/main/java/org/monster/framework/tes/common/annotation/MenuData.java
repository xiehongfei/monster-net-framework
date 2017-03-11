package org.monster.framework.tes.common.annotation;

/**
 * Project :       Monster-frameWork
 * Author:         XIE-HONGFEI
 * Company:        hongfei tld.
 * Created Date:   2017/2/22 0022
 * Copyright @ 2017 Company hongfei tld. – Confidential and Proprietary
 * Desc:
 *      用于注解标识当前Controller方法生成菜单数据的元数据
 * History:
 * ------------------------------------------------------------------------------
 *       Date       |     Author     |   Change Description
 * 2017/2/22 0022   |     谢洪飞      |   初版做成
 */

import java.lang.annotation.*;

/**
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface MenuData {

    /**
     * 菜单路径
     */
    String[] value();

    /**
     * 注释说明：用于描述代码内部用法说明
     */
    String comments() default "";
}
