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

package gaderian.test;

import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Locatable;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.impl.LocationImpl;

/**
 * Tests the method {@link org.ops4j.gaderian.Gaderian#findLocation(Object[])}.
 *
 * @author Howard Lewis Ship
 *
 */
public class TestGaderianFindLocation extends FrameworkTestCase
{
    public void testEmpty()
    {
        assertNull(Gaderian.findLocation(new Object[] {
        }));
    }

    public void testAllNull()
    {
        assertNull(Gaderian.findLocation(new Object[] { null, null, null }));
    }

    public void testOrdering()
    {
        Location l1 = new LocationImpl(null);
        Location l2 = new LocationImpl(null);

        assertSame(l1, Gaderian.findLocation(new Object[] { l1, l2 }));
    }

    public void testLocatable()
    {
        Location l1 = new LocationImpl(null);
        Locatable l2 = new LocatableFixture(l1);

        assertSame(l1, Gaderian.findLocation(new Object[] { l2 }));
    }

    public void testNullLocatable()
    {
        Location l1 = new LocationImpl(null);
        Locatable l2 = new LocatableFixture(null);
        Locatable l3 = new LocatableFixture(l1);

        assertSame(l1, Gaderian.findLocation(new Object[] { l2, l3 }));
    }

    public void testSkipOther()
    {
        Location l1 = new LocationImpl(null);

        assertSame(l1, Gaderian.findLocation(new Object[] { this, "Hello", l1, "Goodbye" }));
    }

    public void testToLocationStringNull()
    {
        assertEquals("unknown location", Gaderian.getLocationString(null));
    }

}
