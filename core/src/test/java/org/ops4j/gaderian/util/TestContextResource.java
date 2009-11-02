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

import java.net.URL;
import java.util.Locale;
import javax.servlet.ServletContext;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.util.ContextResource}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestContextResource extends GaderianCoreTestCase
{
    private ServletContext newContext()
    {
        return createMock(ServletContext.class);
    }

    public void testConstructor()
    {
        ServletContext context = newContext();

        replayAllRegisteredMocks();

        ContextResource r = new ContextResource(context, "/foo/bar/baz_en.html", Locale.ENGLISH);

        assertEquals("context:/foo/bar/baz_en.html", r.toString());

        assertEquals("/foo/bar/baz_en.html", r.getPath());

        assertEquals("baz_en.html", r.getName());

        assertEquals(Locale.ENGLISH, r.getLocale());

        verifyAllRegisteredMocks();
    }

    public void testLocalizationExists() throws Exception
    {
        ServletContext context = newContext();

        expect(context.getResource("/foo/bar/baz_en.html")).andReturn(new URL("http://foo.com"));

        replayAllRegisteredMocks();

        ContextResource r1 = new ContextResource(context, "/foo/bar/baz.html");

        Resource r2 = r1.getLocalization(Locale.ENGLISH);

        assertEquals("/foo/bar/baz_en.html", r2.getPath());
        assertEquals(Locale.ENGLISH, r2.getLocale());

        verifyAllRegisteredMocks();
    }

    public void testLocalizationSame() throws Exception
    {
        ServletContext context = newContext();

        expect(context.getResource("/foo/bar/baz_en.html")).andReturn(null);

        expect(context.getResource("/foo/bar/baz.html")).andReturn(new URL("http://foo.com"));

        replayAllRegisteredMocks();

        ContextResource r1 = new ContextResource(context, "/foo/bar/baz.html");

        Resource r2 = r1.getLocalization(Locale.ENGLISH);

        assertSame(r2, r1);

        verifyAllRegisteredMocks();
    }

    public void testLocalizationMissing() throws Exception
    {
        ServletContext context = newContext();

        expect(context.getResource("/foo/bar/baz_en.html")).andReturn(null);

        expect(context.getResource("/foo/bar/baz.html")).andReturn(null);

        replayAllRegisteredMocks();

        ContextResource r1 = new ContextResource(context, "/foo/bar/baz.html");

        assertNull(r1.getLocalization(Locale.ENGLISH));

        verifyAllRegisteredMocks();
    }

    public void testGetRelativeResource()
    {
        ServletContext context = newContext();

        replayAllRegisteredMocks();

        ContextResource r1 = new ContextResource(context, "/foo/bar/baz.html");
        Resource r2 = r1.getRelativeResource("baz.gif");

        assertEquals("/foo/bar/baz.gif", r2.getPath());

        verifyAllRegisteredMocks();
    }
}