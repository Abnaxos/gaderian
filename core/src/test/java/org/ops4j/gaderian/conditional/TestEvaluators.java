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
 * Tests for {@link org.ops4j.gaderian.conditional.PropertyEvaluator}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestEvaluators extends GaderianCoreTestCase
{
    private EvaluationContext newContext()
    {
        return createMock(EvaluationContext.class);
    }

    private Node newNode(EvaluationContext context, boolean value)
    {
        Node node = newNode( );

        expect(node.evaluate(context)).andReturn( value );

        return node;
    }

    private Node newNode()
    {
        return createMock(Node.class);
    }

    public void testPropertyEvaluator()
    {
        EvaluationContext context = createMock(EvaluationContext.class);

        expect(context.isPropertySet("foo.bar")).andReturn( true );

        replayAllRegisteredMocks();

        PropertyEvaluator pe = new PropertyEvaluator("foo.bar");

        assertEquals(true, pe.evaluate(context, null));

        verifyAllRegisteredMocks();
    }

    public void testClassNameEvaluator()
    {
        EvaluationContext context = createMock(EvaluationContext.class);

        expect(context.doesClassExist("foo.bar.Baz")).andReturn( true );

        replayAllRegisteredMocks();

        ClassNameEvaluator e = new ClassNameEvaluator("foo.bar.Baz");

        assertEquals(true, e.evaluate(context, null));

        verifyAllRegisteredMocks();
    }

    public void testNotEvaluator()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);

        Node node = new NodeImpl(left, null, new NotEvaluator());

        replayAllRegisteredMocks();

        assertEquals(false, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

    public void testAndEvaluatorTrue()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);
        Node right = newNode(context, true);

        Node node = new NodeImpl(left, right, new AndEvaluator());

        replayAllRegisteredMocks();

        assertEquals(true, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

    public void testAndEvaluatorShortcicuit()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, false);
        Node right = newNode();

        Node node = new NodeImpl(left, right, new AndEvaluator());

        replayAllRegisteredMocks();

        assertEquals(false, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

    public void testAndEvaluatorFalse()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);
        Node right = newNode(context, false);

        Node node = new NodeImpl(left, right, new AndEvaluator());

        replayAllRegisteredMocks();

        assertEquals(false, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

    public void testOrEvaluatorTrue()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, false);
        Node right = newNode(context, true);

        Node node = new NodeImpl(left, right, new OrEvaluator());

        replayAllRegisteredMocks();

        assertEquals(true, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

    public void testOrEvaluatorShortcicuit()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, true);
        Node right = newNode();

        Node node = new NodeImpl(left, right, new OrEvaluator());

        replayAllRegisteredMocks();

        assertEquals(true, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

    public void testOrEvaluatorFalse()
    {
        EvaluationContext context = newContext();
        Node left = newNode(context, false);
        Node right = newNode(context, false);

        Node node = new NodeImpl(left, right, new OrEvaluator());

        replayAllRegisteredMocks();

        assertEquals(false, node.evaluate(context));

        verifyAllRegisteredMocks();
    }

}