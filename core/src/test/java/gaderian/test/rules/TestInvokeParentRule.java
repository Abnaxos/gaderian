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

import java.util.List;

import gaderian.test.FrameworkTestCase;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.rules.InvokeParentRule;

public class TestInvokeParentRule extends FrameworkTestCase
{

    public void testInvokeFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("InvokeFailure.xml", false );

        try
        {
            List l = r.getConfiguration("gaderian.test.rules.InvokeFailure");

            l.size();

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to construct configuration gaderian.test.rules.InvokeFailure: Error invoking method failure on org.ops4j.gaderian.impl.SchemaProcessorImpl");

            Throwable inner = findNestedException(ex);
            assertExceptionSubstring(inner, "failure");
        }

    }

    public void testNullParameter() throws Exception
    {
        InvokeParentRule rule = new InvokeParentRule("add");

        SchemaProcessor proc = createMock(SchemaProcessor.class);

        expect(proc.peek()).andReturn(null);

        List list  = createMock(List.class);

        expect(proc.peek(1)).andReturn(list);

        expect(list.add(null)).andReturn(true);

        replayAllRegisteredMocks();

        rule.begin(proc, null);

        verifyAllRegisteredMocks();

        rule = new InvokeParentRule("get");

        expect(proc.peek()).andReturn( null );

        expect(proc.peek(1)).andReturn(list);

        replayAllRegisteredMocks();

        try
        {
            rule.begin(proc, null);

            fail();
        }
        catch (ApplicationRuntimeException e)
        {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }

        verifyAllRegisteredMocks();
    }

    public void testGetMethod()
    {
        InvokeParentRule r = new InvokeParentRule();

        r.setMethodName("foo");

        assertEquals("foo", r.getMethodName());
    }

}
