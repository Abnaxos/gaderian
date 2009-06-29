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

package org.ops4j.gaderian;

import org.ops4j.gaderian.test.GaderianTestCase;

/**
 * Tests some features of {@link org.ops4j.gaderian.ApplicationRuntimeException}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestApplicationRuntimeException extends GaderianTestCase
{
    public void testToStringNoLocation()
    {
        ApplicationRuntimeException ex = new ApplicationRuntimeException("some message");

        assertEquals("org.ops4j.gaderian.ApplicationRuntimeException: some message", ex.toString());
    }

    public void testToStringWithLocation()
    {
        Location l = newLocation();

        ApplicationRuntimeException ex = new ApplicationRuntimeException("my message", l, null);

        assertEquals(
                "org.ops4j.gaderian.ApplicationRuntimeException: my message [" + l + "]",
                ex.toString());
    }
}