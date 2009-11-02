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

package org.ops4j.gaderian.service.impl;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

public class TestBuilderPropertyFacet extends GaderianCoreTestCase
{
    public void testCachingOfTranslatedValues() throws Exception
    {
        Module module = createMock(Module.class);

        Translator translator = createMock(Translator.class);

        ServiceImplementationFactoryParameters params = createMock(ServiceImplementationFactoryParameters.class);

        BuilderPropertyFacet facet = new BuilderPropertyFacet();

        facet.setTranslator("foo");
        facet.setValue("bar");

        expect(params.getInvokingModule()).andReturn(module).times( 2 );

        expect(module.getTranslator("foo")).andReturn(translator);

        expect(translator.translate(module, Object.class, "bar", null)).andReturn("BAR");

        replayAllRegisteredMocks();

        facet.isAssignableToType(params, Object.class);
        facet.getFacetValue(params, Object.class);

        verifyAllRegisteredMocks();
    }

    public void testAssignableFromBadValue()
    {
        Module module = createMock(Module.class);

        Translator translator = createMock(Translator.class);

        ServiceImplementationFactoryParameters params = createMock(ServiceImplementationFactoryParameters.class);

        BuilderPropertyFacet facet = new BuilderPropertyFacet();

        facet.setTranslator("foo");
        facet.setValue("bar");

        expect(params.getInvokingModule()).andReturn(module).times( 2 );

        expect(module.getTranslator("foo")).andReturn(translator);

        ApplicationRuntimeException exception = new ApplicationRuntimeException("");
        expect(translator.translate(module, Object.class, "bar", null)).andThrow( exception );

        replayAllRegisteredMocks();

        try
        {
            facet.isAssignableToType(params, Object.class);
            unreachable();
        }
        catch (ApplicationRuntimeException e)
        {
            assertSame(exception, e);
        }

        verifyAllRegisteredMocks();
    }
}
