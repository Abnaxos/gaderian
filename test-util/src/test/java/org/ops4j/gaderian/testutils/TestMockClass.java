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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;

/**
 * Tests {@link GaderianTestCase}'s ability to generate a mock for a class
 * as well as an interface.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestMockClass extends GaderianTestCase
{
    public void testMockForClass()
    {
        // Skip the test under a Gump build; there's no way to reconcile the different versions
        // of ASM needed by Groovy and easymockclassextension.

        try
        {
            List l = createMock( ArrayList.class );
            

            EasyMock.expect(l.size()).andReturn(5);
            

            replayAllRegisteredMocks();

            // We're not actually testing the List, we're testing the ability to create a mock
            // for ArrayList

            assertEquals(5, l.size());

            verifyAllRegisteredMocks();
        }
        catch (Error err)
        {
            System.err
                    .println("TestMockClass.testMockForClass() failed --- this is due to a conflict in versions of ASM between easymock and groovy.");
            err.printStackTrace();
        }
    }

}