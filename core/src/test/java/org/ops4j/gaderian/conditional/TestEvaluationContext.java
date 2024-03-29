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

package org.ops4j.gaderian.conditional;

import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.conditional.EvaluationContextImpl}.
 *
 * @author Howard M. Lewis Ship
 */
public class TestEvaluationContext extends GaderianCoreTestCase
{
    public void testOperatingSystemProperty()
    {
        EvaluationContext ec = new EvaluationContextImpl(getClassResolver());
        final String osName = System.getProperty( "os.name", "generic" ).toLowerCase();
        assertEquals(osName,ec.getOperatingSystemProperty( "generic" ));
    }

    public void testProperty()
    {
        EvaluationContext ec = new EvaluationContextImpl(getClassResolver());

        System.setProperty("property-set-for-evaluation-context", "true");

        assertEquals(true, ec.isPropertySet("property-set-for-evaluation-context"));
        assertEquals(false, ec.isPropertySet("this-property-does-not-exist"));
    }

    public void testClass()
    {
        EvaluationContext ec = new EvaluationContextImpl(getClassResolver());

        assertEquals(true, ec.doesClassExist("java.lang.Object"));
        assertEquals(true, ec.doesClassExist(EvaluationContext.class.getName()));
        assertEquals(false, ec.doesClassExist("org.ops4j.gaderian.NoSuchClass"));
    }
}