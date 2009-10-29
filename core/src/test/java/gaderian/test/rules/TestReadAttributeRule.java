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

import org.ops4j.gaderian.impl.ElementImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.rules.NullTranslator;
import org.ops4j.gaderian.schema.rules.ReadAttributeRule;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.easymock.MockControl;

public class TestReadAttributeRule extends GaderianCoreTestCase
{
    public void testReadAttributeRule()
    {
        ElementImpl element = new ElementImpl();
        element.setElementName("myelement");

        ReadAttributeRule rule = new ReadAttributeRule();

        rule.setAttributeName("fred");
        rule.setPropertyName("value");

        MockControl control = newControl(SchemaProcessor.class);
        SchemaProcessor mockProcessor = (SchemaProcessor) control.getMock();

        mockProcessor.getAttributeDefault("fred");
        control.setReturnValue("${flintstone}");

        mockProcessor.getContributingModule();

        MockControl moduleControl = newControl(Module.class);
        Module mockModule = (Module) moduleControl.getMock();

        control.setReturnValue(mockModule);

        mockProcessor.peek();

        StringHolderImpl target = new StringHolderImpl();
        control.setReturnValue(target);

        mockProcessor.getAttributeTranslator("fred");
        control.setReturnValue(new NullTranslator());

        mockProcessor.getContributingModule();
        control.setReturnValue(mockModule);

        mockModule.expandSymbols("${flintstone}", element.getLocation());
        moduleControl.setReturnValue("FLINTSTONE");

        replayControls();

        rule.begin(mockProcessor, element);
        rule.end(mockProcessor, element);

        verifyControls();

        assertEquals("FLINTSTONE", target.getValue());
    }
}
