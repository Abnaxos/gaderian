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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.servlet.ServletContext;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Test for {@link org.ops4j.gaderian.util.LocalizedResourceFinder}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestLocalizedContextResourceFinder extends GaderianCoreTestCase
{
    public void testFound() throws Exception
    {
        ServletContext sc = createMock(ServletContext.class);

        expect(sc.getResource("/foo/bar/baz_en_US.html")).andReturn( null );

        expect(sc.getResource("/foo/bar/baz_en.html")).andReturn( new URL("http://foo.com"));

        replayAllRegisteredMocks();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        LocalizedResource lr = f.resolve("/foo/bar/baz.html", Locale.US);

        assertEquals("/foo/bar/baz_en.html", lr.getResourcePath());
        assertEquals(Locale.ENGLISH, lr.getResourceLocale());

        verifyAllRegisteredMocks();

    }

    public void testNotFound() throws Exception
    {
        ServletContext sc = createMock(ServletContext.class);

        expect(sc.getResource("/foo/bar/baz_en.html")).andReturn(null);

        expect(sc.getResource("/foo/bar/baz.html")).andReturn(null);

        replayAllRegisteredMocks();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        assertNull(f.resolve("/foo/bar/baz.html", Locale.ENGLISH));

        verifyAllRegisteredMocks();
    }

    public void testNotFoundException() throws Exception
    {
        ServletContext sc = createMock(ServletContext.class);

        expect(sc.getResource("/foo/bar/baz_en.html")).andThrow(new MalformedURLException());

        expect(sc.getResource("/foo/bar/baz.html")).andThrow(new MalformedURLException());

        replayAllRegisteredMocks();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        assertNull(f.resolve("/foo/bar/baz.html", Locale.ENGLISH));

        verifyAllRegisteredMocks();
    }

    public void testExtensionlessResource() throws Exception
    {
        ServletContext sc = createMock(ServletContext.class);

        expect(sc.getResource("/foo/bar/baz")).andReturn(new URL("http://foo.com"));

        replayAllRegisteredMocks();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        LocalizedResource lr = f.resolve("/foo/bar/baz", null);

        assertEquals("/foo/bar/baz", lr.getResourcePath());
        assertNull(lr.getResourceLocale());

        verifyAllRegisteredMocks();
    }
}