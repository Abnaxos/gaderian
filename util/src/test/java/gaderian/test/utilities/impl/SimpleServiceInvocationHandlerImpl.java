package gaderian.test.utilities.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Johan Lindquist
 */
public class SimpleServiceInvocationHandlerImpl implements InvocationHandler
{
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
  {
    Integer a = (Integer) args[0];
    Integer b = (Integer) args[1];
    return a+b;
  }
}
