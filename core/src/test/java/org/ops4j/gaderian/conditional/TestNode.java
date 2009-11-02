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

package org.ops4j.gaderian.conditional;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for the {@link org.ops4j.gaderian.conditional.NodeImpl} class.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestNode extends GaderianCoreTestCase
{
    public void testConstructorAndGetters()
    {
        Node left = (Node) createMock(Node.class);
        Node right = (Node) createMock(Node.class);
        Evaluator evaluator = (Evaluator) createMock(Evaluator.class);

        replayAllRegisteredMocks();

        Node n = new NodeImpl(left, right, evaluator);

        assertSame(left, n.getLeft());
        assertSame(right, n.getRight());

        verifyAllRegisteredMocks();
    }

    public void testEvaluate()
    {
        Evaluator evaluator = createMock(Evaluator.class);
        EvaluationContext context = createMock(EvaluationContext.class);

        Node n = new NodeImpl(evaluator);

        expect(evaluator.evaluate(context, n)).andReturn( false);

        expect(evaluator.evaluate(context, n)).andReturn( true);

        replayAllRegisteredMocks();

        assertEquals(false, n.evaluate(context));
        assertEquals(true, n.evaluate(context));

        verifyAllRegisteredMocks();
    }
}