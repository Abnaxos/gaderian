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

package gaderian.test.utilities;

import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.test.GaderianTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.utilities.impl.JavaProxyFactory}.
 *
 * @author Johan Lindquist
 */

public class TestJavaProxyFactory extends GaderianTestCase
{
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testJDKProxy() throws Exception
    {
        Registry registry = buildFrameworkRegistry("JavaProxyFactory.xml", false );
        SimpleService simpleService = (SimpleService) registry.getService(SimpleService.class);
        assertEquals(7, simpleService.add(4, 3));
        // Exercise several code paths where objects are ready or cached.
        assertEquals(201, simpleService.add(1, 200));
    }


}