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

package org.ops4j.gaderian.schema.rules;

import org.easymock.Capture;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.schema.rules.CreateObjectRule}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestCreateObjectRule extends GaderianCoreTestCase
{
    private Module newModule(String className, Class result)
    {
        Module module = createMock(Module.class);

        expect(module.resolveType(className)).andReturn( result);

        return module;
    }

    private Element newElement(Location location)
    {
        Element element  = createMock(Element.class);

        expect(element.getLocation()).andReturn( location );

        return element;
    }

    public void testCreateWithInitializer()
    {
        final Location l = newLocation();
        Module module = newModule("Bean", Bean.class);
        Element element = newElement(l);

        SchemaProcessor processor = createMock(SchemaProcessor.class);

        expect(processor.getDefiningModule()).andReturn( module );

        final Capture<Bean> capture = new Capture<Bean>();
        processor.push(EasyMock.capture( capture ));
        replayAllRegisteredMocks();

        CreateObjectRule rule = new CreateObjectRule("Bean,value=Gaderian");

        rule.begin(processor, element);

        verifyAllRegisteredMocks();

        Bean b = capture.getValue();

        assertEquals("Gaderian", b.getValue());
        assertSame(l, b.getLocation());
                
        capture.getValue();

        expect(processor.pop()).andReturn( null );

        replayAllRegisteredMocks();

        rule.end(processor, element);

        verifyAllRegisteredMocks();
    }
}