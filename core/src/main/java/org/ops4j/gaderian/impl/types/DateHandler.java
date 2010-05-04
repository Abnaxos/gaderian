package org.ops4j.gaderian.impl.types;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.internal.Module;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DateHandler implements TypeHandler
{

    private static final Pattern DATE_RE = Pattern.compile( "(\\d\\d\\d\\d)-(\\d?\\d)-(\\d?\\d)" );
    private static final Pattern TIME_RE = Pattern.compile( "(\\d?\\d):(\\d\\d)(:(\\d\\d)(.(\\d\\d\\d))?)?" );

    public Object stringToObject( Module module, String input, Location location ) throws Exception
    {
        // TODO: full support for ISO 8601?
        String dateTime = input.trim();
        int pos = dateTime.indexOf( ' ' );
        Calendar cal;
        if( pos >= 0 )
        {
            String dateString = dateTime.substring( 0, pos );
            while( Character.isWhitespace( ++pos ) )
            {
                // skip
            }
            Matcher date = DATE_RE.matcher( dateString );
            if( !date.matches() )
            {
                // TODO: i18n
                throw new IllegalArgumentException( "Invalid date String '" + input + "'" );
            }
            Matcher time = TIME_RE.matcher( dateTime.substring( pos ) );
            if( !time.matches() )
            {
                // TODO: i18n
                throw new IllegalArgumentException( "Invalid date String '" + input + "'" );
            }
            cal = createCalendar();
            date( cal, date );
            time( cal, time );
        }
        else
        {
            Matcher date = DATE_RE.matcher( dateTime );
            if( date.matches() )
            {
                cal = createCalendar();
                date( cal, date );
            }
            else
            {
                Matcher time = TIME_RE.matcher( dateTime );
                if( !time.matches() )
                {
                    // TODO: i18n
                    throw new IllegalArgumentException( "Invalid date String '" + input + "'" );
                }
                cal = createCalendar();
                time( cal, time );
            }
        }
        return cal.getTime();
    }

    private static Calendar createCalendar()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( 0 );
        return cal;
    }

    private static void date( Calendar target, Matcher matcher )
    {
        target.set( Calendar.YEAR, Integer.parseInt( matcher.group( 1 ) ) );
        target.set( Calendar.MONTH, Integer.parseInt( matcher.group( 2 ) ) );
        target.set( Calendar.DAY_OF_MONTH, Integer.parseInt( matcher.group( 3 ) ) );
    }

    private static void time( Calendar target, Matcher matcher )
    {
        target.set( Calendar.HOUR_OF_DAY, Integer.parseInt( matcher.group( 1 ) ) );
        target.set( Calendar.MINUTE, Integer.parseInt( matcher.group( 2 ) ) );
        String s = matcher.group( 4 );
        if( s != null )
        {
            target.set( Calendar.SECOND, Integer.parseInt( s ) );
            s = matcher.group( 6 );
            if( s != null )
            {
                target.set( Calendar.MILLISECOND, Integer.parseInt( s ) );
            }
        }
    }

}
