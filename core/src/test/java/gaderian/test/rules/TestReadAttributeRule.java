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

package gaderian.test.rules;

import gaderian.test.services.impl.StringHolderImpl;
import static org.easymock.classextension.EasyMock.expect;
import org.ops4j.gaderian.impl.ElementImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.rules.NullTranslator;
import org.ops4j.gaderian.schema.rules.ReadAttributeRule;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

public class TestReadAttributeRule extends GaderianCoreTestCase
{
    public void testReadAttributeRule()
    {
        final ElementImpl element = new ElementImpl();
        element.setElementName("myelement");

        final ReadAttributeRule rule = new ReadAttributeRule();

        rule.setAttributeName("fred");
        rule.setPropertyName("value");

        final SchemaProcessor mockSchemaProcessor = createMock(SchemaProcessor.class);

        expect(mockSchemaProcessor.getAttributeDefault("fred")).andReturn("${flintstone}");

        final Module mockModule = createMock(Module.class);
        expect(mockSchemaProcessor.getContributingModule()).andReturn(mockModule).times(2);

        final StringHolderImpl target = new StringHolderImpl();
        expect(mockSchemaProcessor.peek()).andReturn(target);

        expect(mockSchemaProcessor.getAttributeTranslator("fred")).andReturn(new NullTranslator());

        expect(mockModule.expandSymbols("${flintstone}", element.getLocation())).andReturn("FLINTSTONE");

        replayAllRegisteredMocks();

        rule.begin(mockSchemaProcessor, element);
        rule.end(mockSchemaProcessor, element);

        verifyAllRegisteredMocks();

        assertEquals("FLINTSTONE", target.getValue());
    }
}
