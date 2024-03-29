// Copyright 2005 The Apache Software Foundation
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

package org.ops4j.gaderian.examples.setters;

import junit.framework.TestCase;

import org.ops4j.gaderian.examples.ExampleUtils;
import org.ops4j.gaderian.Registry;

/**
 * Runs the setters example.
 *
 * @author Achim Huegen
 */
public class TestSetters extends TestCase
{
    public void testRun() throws Exception
    {
        Registry registry = ExampleUtils
                .buildClasspathRegistry("/org/ops4j/gaderian/examples/setters/setters.xml");
        Runnable setterService = (Runnable) registry.getService("gaderian.examples.setters.Service", Runnable.class);
        setterService.run();
    }
}
