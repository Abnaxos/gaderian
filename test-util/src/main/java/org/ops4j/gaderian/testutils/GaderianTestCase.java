// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ops4j.gaderian.testutils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;

/**
 * Contains some support for creating Gaderian tests; this is useful enough that has been moved into
 * the main framework, to simplify creation of tests in the dependent libraries.
 *
 * @author Howard Lewis Ship
 */
public abstract class GaderianTestCase extends TestCase
{
    protected String _interceptedLoggerName;

    private StoreAppender _appender;

    /** List containing the current list of mock objects which have been registered with this test case **/
    private List<Object> _registeredMocks = new ArrayList<Object>();


    /** Creates a EasyMock mock object for the specified class
     * @param clazz The class (type) to create the mock for.  This may be either an interface or
     * class (easymock class extensions are used for this).
     * @return The created mock object
     */
    protected <T> T createMock(final Class<T> clazz)
    {
        final T mock = EasyMock.createMock(clazz);
        _registeredMocks.add(mock);
        return mock;
    }

    /** Performs a EasyMock replay on all the registered mock objects
     */
    protected void replayAllRegisteredMocks()
    {
        replay( _registeredMocks.toArray());
    }

    /** Performs a verify & reset on all registered mock objects.
     *
     */
    protected void verifyAllRegisteredMocks()
    {
        verify( _registeredMocks.toArray());
        reset( _registeredMocks.toArray());
    }

    /** Performs a resets on all the registered mock objects.
     *
     */
    protected void resetAllRegisteredMocks()
    {
        reset( _registeredMocks.toArray());
    }

    /**
     * Converts the actual list to an array and invokes
     * {@link #assertListsEqual(Object[], Object[])}.
     */
    protected static void assertListsEqual(Object[] expected, List actual)
    {
        assertListsEqual(expected, actual.toArray());
    }

    /**
     * Asserts that the two arrays are equal; same length and all elements equal. Checks the
     * elements first, then the length.
     */
    protected static void assertListsEqual(Object[] expected, Object[] actual)
    {
        assertNotNull(actual);

        int min = Math.min(expected.length, actual.length);

        for (int i = 0; i < min; i++)
            assertEquals("list[" + i + "]", expected[i], actual[i]);

        assertEquals("list length", expected.length, actual.length);
    }

    /**
     * Called when code should not be reachable (because a test is expected to throw an exception);
     * throws AssertionFailedError always.
     */
    protected static void unreachable()
    {
        throw new AssertionFailedError("This code should be unreachable.");
    }

    protected URL getResourceURL( final String file )
    {
        URL url = getClass().getResource(file);

        if (url == null)
            throw new NullPointerException("No resource named '" + file + "'.");
        return url;
    }

    /**
     * Sets up an appender to intercept logging for the specified logger. Captured log events can be
     * recovered via {@link #getInterceptedLogEvents()}.
     */
    protected void interceptLogging(String loggerName)
    {
        Logger logger = LogManager.getLogger(loggerName);

        logger.removeAllAppenders();

        _interceptedLoggerName = loggerName;
        _appender = new StoreAppender();
        _appender.activateOptions();

        logger.setLevel(Level.DEBUG);
        logger.setAdditivity(false);
        logger.addAppender(_appender);
    }

    /**
     * Gets the list of events most recently intercepted. This resets the appender, clearing the
     * list of stored events.
     *
     * @see #interceptLogging(String)
     */

    protected List<LoggingEvent> getInterceptedLogEvents()
    {
        return _appender.getEvents();
    }

    /**
     * Removes the appender that may have been setup by {@link #interceptLogging(String)}.
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if ( _registeredMocks != null && !_registeredMocks.isEmpty())
        {
            _registeredMocks.clear();
        }

        if (_appender != null)
        {
            _appender = null;

            Logger logger = LogManager.getLogger(_interceptedLoggerName);
            logger.setLevel(null);
            logger.setAdditivity(true);
            logger.removeAllAppenders();
        }

    }

    /**
     * Checks that the provided substring exists in the exceptions message.
     */
    protected void assertExceptionSubstring(Throwable ex, String substring)
    {
        String message = ex.getMessage();
        assertNotNull(message);

        int pos = message.indexOf(substring);

        if (pos < 0)
            throw new AssertionFailedError("Exception message (" + message + ") does not contain ["
                    + substring + "]");
    }

    /**
     * Checks that the message for an exception matches a regular expression.
     */

    protected void assertExceptionRegexp(Throwable ex, String pattern) throws Exception
    {
        String message = ex.getMessage();
        assertNotNull(message);

        Pattern compiled = Pattern.compile(pattern);

        if (compiled.matcher(message).find())
            return;

        throw new AssertionFailedError("Exception message (" + message
                + ") does not contain regular expression [" + pattern + "].");
    }

    protected void assertRegexp(String pattern, String actual) 
    {

        Pattern compiled = Pattern.compile(pattern);

        if (compiled.matcher(actual).find())
            return;

        throw new AssertionFailedError("\"" + actual + "\" does not contain regular expression["
                + pattern + "].");
    }

    /**
     * Checks to see if a specific event matches the name and message.
     *
     * @param message
     *            exact message to search for
     * @param events
     *            the list of events {@link #getInterceptedLogEvents()}
     * @param index
     *            the index to check at
     */
    private void assertLoggedMessage(String message, List events, int index)
    {
        LoggingEvent e = (LoggingEvent) events.get(index);

        assertEquals("Message", message, e.getMessage());
    }

    /**
     * Checks the messages for all logged events for exact match against the supplied list.
     */
    protected void assertLoggedMessages(String[] messages)
    {
        List events = getInterceptedLogEvents();

        for (int i = 0; i < messages.length; i++)
        {
            assertLoggedMessage(messages[i], events, i);
        }
    }

    /**
     * Asserts that some capture log event matches the given message exactly.
     */
    protected void assertLoggedMessage(String message)
    {
        assertLoggedMessage(message, getInterceptedLogEvents());
    }

    /**
     * Asserts that some capture log event matches the given message exactly.
     *
     * @param message
     *            to search for; success is finding a logged message contain the parameter as a
     *            substring
     * @param events
     *            from {@link #getInterceptedLogEvents()}
     */
    protected void assertLoggedMessage(String message, List events)
    {
        int count = events.size();

        for (int i = 0; i < count; i++)
        {
            LoggingEvent e = (LoggingEvent) events.get(i);

            String eventMessage = String.valueOf(e.getMessage());

            if (eventMessage.indexOf(message) >= 0)
                return;
        }

        throw new AssertionFailedError("Could not find logged message: " + message);
    }

    protected void assertLoggedMessagePattern(String pattern) throws Exception
    {
        assertLoggedMessagePattern(pattern, getInterceptedLogEvents());
    }

    protected void assertLoggedMessagePattern(String pattern, List events) throws Exception
    {

        Pattern compiled = null;

        int count = events.size();

        for (int i = 0; i < count; i++)
        {
            LoggingEvent e = (LoggingEvent) events.get(i);

            String eventMessage = e.getMessage().toString();

            if (compiled == null)
                compiled = Pattern.compile(pattern);

            if (compiled.matcher(eventMessage).find())
                return;

        }

        throw new AssertionFailedError("Could not find logged message with pattern: " + pattern);
    }

    protected boolean matches(String input, String pattern) throws Exception
    {
        Pattern compiled = Pattern.compile(pattern);
        return compiled.matcher(input).matches();
    }

}