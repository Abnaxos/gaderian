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

package org.ops4j.gaderian.schema.rules;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.schema.rules.QualifiedIdTranslator}
 * and {@link org.ops4j.gaderian.schema.rules.IdListTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestIdTranslators extends GaderianCoreTestCase
{
    public void testNullId()
    {
        Translator t = new QualifiedIdTranslator();

        assertNull(t.translate(null, null, null, null));
    }

    private Module getModule()
    {
        Module result = createMock(Module.class);

        expect(result.getModuleId()).andReturn( "foo.bar" );

        return result;
    }

    public void testNonNullId()
    {
        Module m = getModule();

        replayAllRegisteredMocks();

        Translator t = new QualifiedIdTranslator();

        assertEquals("foo.bar.Baz", t.translate(m, null, "Baz", null));

        verifyAllRegisteredMocks();
    }

    public void testNullList()
    {
        Translator t = new IdListTranslator();

        assertEquals(null, t.translate(null, null, null, null));
    }

    public void testNonNullList()
    {
        Module m = getModule();

        replayAllRegisteredMocks();

        Translator t = new IdListTranslator();

        assertEquals("foo.bar.Baz,zip.Zap", t.translate(m, null, "Baz,zip.Zap", null));

        verifyAllRegisteredMocks();
    }
}
