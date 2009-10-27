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

package gaderian.test.services;

import java.util.List;

import gaderian.test.FrameworkTestCase;
import gaderian.test.services.impl.SimpleShutdownAwareConcreteService;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Registry;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Tests shutdown on the registry and on deferred and threaded services.
 *
 * @author Howard Lewis Ship
 */
public class TestShutdown extends FrameworkTestCase
{

    public void testShutdownSingleton() throws Exception

    {
        Registry r = buildFrameworkRegistry("SimpleModule.xml", false );
        SimpleService s = (SimpleService) r.getService(
                "gaderian.test.services.Simple",
                SimpleService.class);

        assertEquals(11, s.add(4, 7));

        r.shutdown();

        try
        {
            s.add(9, 5);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }
    }

     public void testShutdownSingletonConcreteClass() throws Exception

    {
        Registry r = buildFrameworkRegistry("SimpleConcreteModule.xml", false );
        SimpleShutdownAwareConcreteService s = (SimpleShutdownAwareConcreteService) r.getService(
                "gaderian.test.services.SimpleShutdownAwareConcreteService",
                SimpleShutdownAwareConcreteService.class);

        assertEquals(11, s.add(4, 7));

        interceptLogging("gaderian.test.services.SimpleShutdownAwareConcreteService");

        r.shutdown();

        final List<LoggingEvent> interceptedLogEvents = getInterceptedLogEvents();
        // There should be 3 messages - instantiation, autowiring and the registry shutdown log message
        assertEquals("bad number of intercepted log events", 1, interceptedLogEvents.size());

        assertLoggedMessage("registryDidShutdown --- SimpleShutdownAwareConcreteService",interceptedLogEvents);

        try
        {
            s.add(9, 5);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }
    }

    public void testShutdownSingletonClassServiceInterfaceExtendingRegistryShutdownListener() throws Exception
    {
       Registry r = buildFrameworkRegistry("SimpleModule.xml", false );
       SimpleShutdownAwareService s = (SimpleShutdownAwareService) r.getService(
               "gaderian.test.services.SimpleShutdownAware",
               SimpleShutdownAwareService.class);



       assertEquals(11, s.add(4, 7));

       interceptLogging("gaderian.test.services.SimpleShutdownAware");

       r.shutdown();

       final List<LoggingEvent> interceptedLogEvents = getInterceptedLogEvents();

       assertEquals("bad number of intercepted log events", 1, interceptedLogEvents.size());
       assertLoggedMessage("registryDidShutdown --- SimpleShutdownAware",interceptedLogEvents);

       try
       {
           s.add(9, 5);
           unreachable();
       }
       catch (ApplicationRuntimeException ex)
       {
           assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
       }
   }


    public void testRegistryShutdownUnrepeatable() throws Exception
    {
        Registry r = buildFrameworkRegistry("SimpleModule.xml", false );

        r.shutdown();

        try
        {
            r.getConfiguration("foo.bar");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }

        try
        {
            r.shutdown();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }
    }

    public void testShutdownThreaded() throws Exception
    {
        Registry r = buildFrameworkRegistry("StringHolder.xml", false );

        StringHolder h = (StringHolder) r.getService(
                "gaderian.test.services.StringHolder",
                StringHolder.class);

        assertNull(h.getValue());

        h.setValue("fred");

        assertEquals("fred", h.getValue());

        r.shutdown();

        try
        {
            h.getValue();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }
    }

    public void testSingletonCore() throws Exception
    {
        Registry r = buildFrameworkRegistry("Shutdown.xml", false );

        Runnable s = (Runnable) r.getService("gaderian.test.services.Singleton", Runnable.class);

        interceptLogging("gaderian.test.services.Singleton");

        s.run();

        assertLoggedMessage("run -- Singleton");

        r.shutdown();

        assertLoggedMessage("registryDidShutdown -- Singleton");
    }

    public void testPrimitiveCore() throws Exception
    {
        Registry r = buildFrameworkRegistry("Shutdown.xml", false );

        Runnable s = (Runnable) r.getService("gaderian.test.services.Primitive", Runnable.class);

        interceptLogging("gaderian.test.services.Primitive");

        s.run();

        assertLoggedMessage("run -- Primitive");

        r.shutdown();

        assertLoggedMessage("registryDidShutdown -- Primitive");
    }

}
