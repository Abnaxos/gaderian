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

import org.ops4j.gaderian.Registry;

/**
 * Tests Registry.contains functionality.
 *
 * @author Naresh Sikha
 */
public class TestContains extends FrameworkTestCase
{
    private Registry registry;

    protected void setUp() throws Exception
    {
    	super.setUp();

        registry = buildFrameworkRegistry("contains.xml", false );
    }

    protected void tearDown() throws Exception
    {
    	super.tearDown();

        registry.shutdown();
    }

    public void testConfiguration()
    {
        assertTrue(registry.containsConfiguration("gaderian.tests.contains.Simple"));
    }

    public void testConfigurationFailure()
    {
        assertTrue(!registry.containsConfiguration("xgaderian.tests.contains.Simple"));
    }

    public void testService()
    {
        assertTrue(registry.containsService("gaderian.tests.contains.multipleServiceOne", IMultipleService.class));
    }

    public void testServiceFailure()
    {
        assertTrue(!registry.containsService("gaderian.tests.contains.multipleServiceOne", IUniqueService.class));
        assertTrue(!registry.containsService("xgaderian.tests.contains.multipleServiceOne", IMultipleService.class));
    }

    public void testUniqueService()
    {
        assertTrue(registry.containsService(IUniqueService.class));
    }

    public void testUniqueServiceFailure()
    {
        assertTrue(!registry.containsService(IMultipleService.class));
    }

}
