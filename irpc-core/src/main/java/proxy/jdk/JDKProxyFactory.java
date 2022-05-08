package proxy.jdk;

import proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @author neilfoc
 * @Description
 * @Date 2022/5/8
 */
public class JDKProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(final Class clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new JDKClientInvocationHandler(clazz));
    }
}
