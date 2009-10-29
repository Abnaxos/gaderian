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

import org.ops4j.gaderian.impl.ServicePointImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.schema.rules.ServicePointTranslator;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.easymock.MockControl;

/**
 * Test for the {@link org.ops4j.gaderian.schema.rules.ServicePointTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestServicePointTranslator extends GaderianCoreTestCase
{
    public void testServicePointTranslator()
    {
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        ServicePoint sp = new ServicePointImpl();

        m.getServicePoint("Fred");
        control.setReturnValue(sp);

        replayControls();

        Translator t = new ServicePointTranslator();

        ServicePoint result = (ServicePoint) t.translate(m, null, "Fred", null);

        assertSame(sp, result);

        verifyControls();
    }
}
