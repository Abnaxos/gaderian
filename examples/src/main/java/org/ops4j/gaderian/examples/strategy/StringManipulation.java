package org.ops4j.gaderian.examples.strategy;

/** Base class used as the signature type in the <code>StringManipulator</code> example.  Simple wraps a string internally
 * which sub-classes of this class may allow to manipulate in some way.
 * @author Johan Lindquist
 */
public abstract class StringManipulation
{
    private String _stringToManipulate;

    public StringManipulation( final String stringToManipulate )
    {
        _stringToManipulate = stringToManipulate;
    }

    public String getStringToManipulate()
    {
        return _stringToManipulate;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final StringManipulation that = ( StringManipulation ) o;

        if ( _stringToManipulate != null ? !_stringToManipulate.equals( that._stringToManipulate ) : that._stringToManipulate != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return _stringToManipulate != null ? _stringToManipulate.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "StringManipulation{" +
                "_stringToManipulate='" + _stringToManipulate + '\'' +
                '}';
    }
}
