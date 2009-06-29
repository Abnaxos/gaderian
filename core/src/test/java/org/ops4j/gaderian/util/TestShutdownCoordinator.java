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

package org.ops4j.gaderian.util;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ShutdownCoordinator;
import org.ops4j.gaderian.events.RegistryShutdownListener;
import org.ops4j.gaderian.impl.RegistryBuilder;
import org.ops4j.gaderian.impl.ShutdownCoordinatorImpl;

import gaderian.test.FrameworkTestCase;

/**
 * Tests the {@link org.ops4j.gaderian.impl.ShutdownCoordinator}.
 *
 * @author Howard Lewis Ship
 */
public class TestShutdownCoordinator extends FrameworkTestCase
{
    private static class Fixture implements RegistryShutdownListener
    {
        private boolean _shutdown;

        public boolean isShutdown()
        {
            return _shutdown;
        }

        public void registryDidShutdown()
        {
            _shutdown = true;
        }

    }

    public void testShutdownCoordinator()
    {
        ShutdownCoordinator c = new ShutdownCoordinatorImpl();

        Fixture f = new Fixture();

        c.addRegistryShutdownListener(f);

        c.shutdown();

        assertEquals(true, f.isShutdown());

        // For good riddens; test no failure if already down.

        c.shutdown();
    }

    public void testShutdownCoordinatorService()
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ShutdownCoordinator c =
            (ShutdownCoordinator) r.getService(
                "gaderian.ShutdownCoordinator",
                ShutdownCoordinator.class);

        Fixture f = new Fixture();

        c.addRegistryShutdownListener(f);

        c.shutdown();

        assertEquals(true, f.isShutdown());
    }

    public void testShutdownFailure() throws Exception
    {
        ShutdownCoordinator c = new ShutdownCoordinatorImpl();

        c.addRegistryShutdownListener(new RegistryShutdownListener()
        {
            public void registryDidShutdown()
            {
                throw new ApplicationRuntimeException("I'm just not in the mood.");
            }
        });

        interceptLogging();

        c.shutdown();

        assertLoggedMessagePattern("Unable to shutdown .*: I'm just not in the mood\\.");
    }

}
