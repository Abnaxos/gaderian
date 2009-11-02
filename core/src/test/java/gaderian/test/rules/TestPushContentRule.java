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

import static org.easymock.classextension.EasyMock.expect;
import org.ops4j.gaderian.impl.ElementImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.rules.NullTranslator;
import org.ops4j.gaderian.schema.rules.PushContentRule;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

public class TestPushContentRule extends GaderianCoreTestCase
{
    public void testPushContentRule()
    {
        final SchemaProcessor mockSchemaProcessor= createMock(SchemaProcessor.class);

        final ElementImpl element = new ElementImpl();
        element.setElementName("myelement");
        element.setContent("${flintstone}");

        final PushContentRule rule = new PushContentRule();

        expect(mockSchemaProcessor.getContentTranslator()).andReturn(new NullTranslator());

        final Module mockModule = createMock(Module.class);
        expect(mockSchemaProcessor.getContributingModule()).andReturn(mockModule).times(2);

        expect(mockModule.expandSymbols("${flintstone}", element.getLocation())).andReturn("FLINTSTONE");

        mockSchemaProcessor.push("FLINTSTONE");

        expect(mockSchemaProcessor.pop()).andReturn("FLINTSTONE");

        replayAllRegisteredMocks();

        rule.begin(mockSchemaProcessor, element);
        rule.end(mockSchemaProcessor, element);

        verifyAllRegisteredMocks();
    }
}
