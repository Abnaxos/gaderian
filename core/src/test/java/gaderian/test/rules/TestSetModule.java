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

package gaderian.test.rules;

import gaderian.test.FrameworkTestCase;

import org.ops4j.gaderian.impl.ModuleImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.rules.SetModuleRule;
import org.easymock.MockControl;

/**
 * Tests the {@link org.ops4j.gaderian.schema.rules.SetModuleRule} rule class.
 *
 * @author Howard Lewis Ship
 */
public class TestSetModule extends FrameworkTestCase
{
    public static class Target
    {
        private Module _module;

        public Module getModule()
        {
            return _module;
        }

        public void setModule(Module module)
        {
            _module = module;
        }

    }

    public void testSetModuleRule()
    {
        MockControl control = newControl(SchemaProcessor.class);
        SchemaProcessor p = (SchemaProcessor) control.getMock();

        Module m = new ModuleImpl();
        Target t = new Target();

        p.peek();
        control.setReturnValue(t);

        p.getContributingModule();
        control.setReturnValue(m);

        replayControls();

        SetModuleRule rule = new SetModuleRule();

        rule.setPropertyName("module");

        rule.begin(p, null);

        assertSame(m, t.getModule());

        verifyControls();
    }
}
