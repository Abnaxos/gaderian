package gaderian.test.services.impl;

import gaderian.test.services.SimpleCovariantService;

/**
 * @author Johan Lindquist
 */
public class SimpleCovariantServiceReturningLong implements SimpleCovariantService
{
  public Long add(final int a, final int b)
  {
    return (long)a+b;
  }

  public Number add(final Number a, final Number b)
  {
    return a.longValue() + b.longValue();
  }

  public Number voidCall()
  {
    return 1L;
  }
}
