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

package org.ops4j.gaderian.utilities.groovy;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.impl.DefaultErrorHandler;
import org.ops4j.gaderian.parse.ModuleDescriptor;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

public class TestGroovyModuleDescriptorProvider extends GaderianCoreTestCase
{
    public void testBasicScript() throws Exception
    {
        GroovyModuleDescriptorProvider provider = new GroovyModuleDescriptorProvider(
                getClassResolver(), getResource("basic.groovy"));

        ErrorHandler errorHandler = new DefaultErrorHandler();

        List descriptors = provider.getModuleDescriptors(errorHandler);

        assertEquals(1, descriptors.size());

        ModuleDescriptor descriptor = (ModuleDescriptor) descriptors.get(0);

        assertEquals("basic", descriptor.getModuleId());
    }

    public void testMultipleResources() throws Exception
    {
        List resources = new ArrayList();

        resources.add(getResource("basic.groovy"));
        resources.add(getResource("calculator.groovy"));

        GroovyModuleDescriptorProvider provider = new GroovyModuleDescriptorProvider(
                getClassResolver(), resources);

        ErrorHandler errorHandler = new DefaultErrorHandler();

        List descriptors = provider.getModuleDescriptors(errorHandler);

        assertEquals(2, descriptors.size());
    }
}