package proxy;

/**
 * @author neilfoc
 * @Description
 * @Date 2022/5/8
 */
public interface ProxyFactory {

    <T> T getProxy(final Class clazz) throws Throwable;
}
