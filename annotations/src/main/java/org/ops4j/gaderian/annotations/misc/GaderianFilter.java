package org.ops4j.gaderian.annotations.misc;

import java.lang.annotation.*;

/**
 * @author Johan Lindquist
 */
@Documented
@Retention(value = RetentionPolicy.SOURCE)
@Target(value = ElementType.TYPE)
public @interface GaderianFilter
{
    boolean generateAbstract() default false;
}
