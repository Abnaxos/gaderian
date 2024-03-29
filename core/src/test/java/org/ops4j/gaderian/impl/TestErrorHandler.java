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

package org.ops4j.gaderian.impl;

import gaderian.test.FrameworkTestCase;
import org.apache.commons.logging.Log;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.util.ClasspathResource;

/**
 * Tests for {@link org.ops4j.gaderian.impl.DefaultErrorHandler}.
 *
 * @author Howard Lewis Ship
 */
public class TestErrorHandler extends FrameworkTestCase
{
    public void testDefaultErrorHandlerWithLocation()
    {
        Log log = (Log) createMock(Log.class);

        Resource r = new ClasspathResource(getClassResolver(), "/foo/bar/Baz.module");
        Location l = new LocationImpl(r, 13);

        Throwable ex = new IllegalArgumentException();

        log.error("Error at classpath:/foo/bar/Baz.module, line 13: Bad frob value.", ex);

        replayAllRegisteredMocks();

        ErrorHandler eh = new DefaultErrorHandler();

        eh.error(log, "Bad frob value.", l, ex);

        verifyAllRegisteredMocks();
    }

    public void testDefaultErrorHandlerWithNoLocation()
    {
        Log log = (Log) createMock(Log.class);

        Throwable ex = new IllegalArgumentException();

        log.error("Error: Bad frob value.", ex);

        replayAllRegisteredMocks();

        ErrorHandler eh = new DefaultErrorHandler();

        eh.error(log, "Bad frob value.", null, ex);

        verifyAllRegisteredMocks();
    }

    public void testStrictErrorHandler()
    {
        ErrorHandler eh = new StrictErrorHandler();
        Throwable cause = new NullPointerException();

        try
        {
            eh.error(null, "An error message.", null, cause);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Error: An error message.", ex.getMessage());
            assertSame(cause, ex.getRootCause());
        }
    }

    public void testStrictErrorHandlerWithLocation()
    {
        ErrorHandler eh = new StrictErrorHandler();
        Throwable cause = new NullPointerException();
        Location l = newLocation();

        try
        {
            eh.error(null, "An error message.", l, cause);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Error at " + l + ": An error message.", ex.getMessage());
            assertSame(l, ex.getLocation());
            assertSame(cause, ex.getRootCause());
        }
    }
}
