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

package org.ops4j.gaderian.service.impl;

import java.util.Collections;

import org.easymock.EasyMock;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.impl.TestTypeConverter;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.rules.SmartTranslator;
import org.ops4j.gaderian.service.ObjectProvider;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.service.impl.ObjectTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestObjectTranslator extends GaderianCoreTestCase
{

    public void testWithoutPrefix()
    {
        ObjectTranslator ot = new ObjectTranslator();

        Module module = createMock(Module.class);

        ot.setContributions(Collections.EMPTY_MAP);

        EasyMock.expect(module.getTranslator("smart")).andReturn(new SmartTranslator( TestTypeConverter.createTestConverter()));

        replayAllRegisteredMocks();

        Object result = ot.translate(module, Object.class, "99", null);

        assertEquals("99", result);

        verifyAllRegisteredMocks();
    }

    public void testUnknownPrefix()
    {
        ObjectTranslator ot = new ObjectTranslator();

        Module module = createMock(Module.class);

        ErrorLog el = createMock(ErrorLog.class);

        ot.setErrorLog(el);
        ot.setContributions(Collections.EMPTY_MAP);

        el.error("No object provider exists for prefix 'zap'.", null, null);

        replayAllRegisteredMocks();

        Object result = ot.translate(module, Object.class, "zap:foo", null);

        assertNull(result);

        verifyAllRegisteredMocks();
    }

    public void testSuccess()
    {
        ObjectProvider p = createMock(ObjectProvider.class);

        ObjectTranslator ot = new ObjectTranslator();
        ot.setContributions(Collections.singletonMap("fetch", p));

        Module module = createMock(Module.class);

        Object value = new Integer(13);

        EasyMock.expect(p.provideObject(module, Integer.class, "zap", null)).andReturn( value );



        replayAllRegisteredMocks();

        Object result = ot.translate(module, Integer.class, "fetch:zap", null);

        assertSame(value, result);

        verifyAllRegisteredMocks();
    }

    public void testNullInput()
    {
        ObjectTranslator ot = new ObjectTranslator();

        assertNull(ot.translate(null, null, null, null));
    }
}