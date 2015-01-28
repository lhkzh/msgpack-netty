package u14.reflect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
/**
 * 忽略的方法
 * @author zhangheng
 */
public @interface JxOmitMethod {

}
