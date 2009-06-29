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

import gaderian.test.services.SimpleService;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Registry;

/**
 * Tests the ability for a module to include &lt;sub-module&gt;s.
 * 
 * @author Howard Lewis Ship
 */
public class TestSubModule extends FrameworkTestCase
{
    public void testNestedSubModule() throws Exception
    {
        Registry r = buildFrameworkRegistry("OuterModule.xml");

        SimpleService s = (SimpleService) r.getService(
                "gaderian.test.outer.Simple",
                SimpleService.class);

        assertEquals(11, s.add(4, 7));

    }

    public void testMissingSubModule() throws Exception
    {
        interceptLogging();

        buildFrameworkRegistry("MissingSubModule.xml");

        assertLoggedMessagePattern("Sub-module .*?/DoesNotExist\\.xml does not exist\\.");
    }

    public void testConditionalSubModuleIncluded() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConditionalIncludedSubmodule.xml");
        SimpleService s = (SimpleService) r.getService(
                "gaderian.test.outer.Simple",
                SimpleService.class);
        assertEquals(11, s.add(4, 7));
    }

    public void testConditionalSubModuleNotIncluded() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConditionalNotIncludedSubmodule.xml");

        try
        {
            r.getService("gaderian.test.outer.Simple", SimpleService.class);

            fail("Service point gaderian.test.outer.Simple should not exist.");
        }
        catch (ApplicationRuntimeException e)
        {
            assertEquals("Service point gaderian.test.outer.Simple does not exist.", e.getMessage());
        }
    }

}