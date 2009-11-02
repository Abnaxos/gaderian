package org.ops4j.gaderian.factory;

/**
 * @author Johan Lindquist
 */
public class SimpleNoArgConstructorBean
{
    private String _stringValue;
    private int _intValue;

    public SimpleNoArgConstructorBean()
    {
    }

    public String getStringValue()
    {
        return _stringValue;
    }

    public void setStringValue(final String stringValue)
    {
        _stringValue = stringValue;
    }


    public int getIntValue()
    {
        return _intValue;
    }

    public void setIntValue(final int intValue)
    {
        _intValue = intValue;
    }

    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final SimpleNoArgConstructorBean that = (SimpleNoArgConstructorBean) o;

        if (_intValue != that._intValue)
        {
            return false;
        }
        if (_stringValue != null ? !_stringValue.equals(that._stringValue) : that._stringValue != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (_stringValue != null ? _stringValue.hashCode() : 0);
        result = 31 * result + _intValue;
        return result;
    }
}
