// Copyright 2005 The Apache Software Foundation
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

package org.ops4j.gaderian;

import java.util.Locale;

import org.ops4j.gaderian.impl.RegistryBuilder;
import org.ops4j.gaderian.service.ThreadLocale;
import org.ops4j.gaderian.test.GaderianTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.service.ThreadLocale} service. We revert to using
 * integration tests because unit tests would not be very meaningful.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestThreadLocale extends GaderianTestCase
{
    public void testThreadSpecific() throws Exception
    {
        final Registry r = RegistryBuilder.constructDefaultRegistry();

        final ThreadLocale tl = (ThreadLocale) r.getService(ThreadLocale.class);

        assertSame(r.getLocale(), tl.getLocale());

        tl.setLocale(Locale.KOREAN);

        assertSame(Locale.KOREAN, tl.getLocale());

        Thread t = new Thread()
        {
            public void run()
            {
                assertSame(r.getLocale(), tl.getLocale());
            }
        };

        t.start();
        t.join();
    }

    public void testResetOnThreadCleanup() throws Exception
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ThreadLocale tl = (ThreadLocale) r.getService(ThreadLocale.class);

        Locale start = r.getLocale();

        assertSame(start, tl.getLocale());

        tl.setLocale(Locale.CANADA_FRENCH);

        r.cleanupThread();

        assertSame(start, tl.getLocale());
    }

}