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

package org.ops4j.gaderian.service.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.find;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.service.ClassFactory;
import org.ops4j.gaderian.service.InterfaceFab;
import org.ops4j.gaderian.service.MethodSignature;
import org.ops4j.gaderian.testutils.GaderianTestCase;

/**
 * @author Howard M. Lewis Ship
 */
public class TestInterfaceSynthesizer extends GaderianTestCase
{
    private ClassFactory newClassFactory(InterfaceFab fab)
    {
        return createMock(ClassFactory.class);
    }

    public void testSimple()
    {
        InterfaceFab fab = createMock(InterfaceFab.class);

        ClassFactory cf = newClassFactory(fab);

        fab.addMethod(new MethodSignature(void.class, "doStuff", null, null));

        expect( cf.newInterface( find("\\$SimpleBean_"))).andReturn(fab);

        expect(fab.createInterface()).andReturn(null);

        replayAllRegisteredMocks();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(SimpleBean.class);

        verifyAllRegisteredMocks();


    }

    public void testExtendingInterface()
    {
        InterfaceFab fab = createMock(InterfaceFab.class);

        ClassFactory cf = newClassFactory(fab);

        fab.addInterface(BeanInterface.class);
        fab.addMethod(new MethodSignature(String.class, "beanMethod", null, null));

        expect( cf.newInterface( find("\\$ExtendingInterfaceBean_"))).andReturn(fab);

        expect(fab.createInterface()).andReturn(null);

        replayAllRegisteredMocks();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(ExtendingInterfaceBean.class);

        verifyAllRegisteredMocks();

    }

    public void testExtendingSubInterface()
    {
        // We can't predict the order in which the methods will be invoked (it's based
        // on things like hash values and order of objects inside a Set), so
        // we use a nice control.

        InterfaceFab fab = createMock(InterfaceFab.class);

        ClassFactory cf = newClassFactory(fab);

        fab.addInterface(BeanInterface.class);
        fab.addInterface(BeanSubInterface.class);
        fab.addMethod(new MethodSignature(String.class, "beanMethod", null, null));

        expect( cf.newInterface( find("\\$ExtendingSubInterfaceBean_"))).andReturn(fab);

        expect(fab.createInterface()).andReturn(null);

        replayAllRegisteredMocks();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(ExtendingSubInterfaceBean.class);

        verifyAllRegisteredMocks();

    }

    public void testIgnoreStaticAndPrivateMethods()
    {
        InterfaceFab fab = createMock(InterfaceFab.class);

        ClassFactory cf = newClassFactory(fab);

        fab.addInterface(BeanInterface.class);
        fab.addMethod(new MethodSignature(String.class, "beanMethod", null, null));

        expect( cf.newInterface( find("\\$IgnoreStaticAndPrivateMethodsBean_"))).andReturn(fab);

        expect(fab.createInterface()).andReturn(null);

        replayAllRegisteredMocks();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(IgnoreStaticAndPrivateMethodsBean.class);

        verifyAllRegisteredMocks();
    }

    /**
     * Adds test for attempts to create a proxy for a class containing a final method
     */
    public void testHandleFinalMethods()
    {
        InterfaceFab fab = createMock(InterfaceFab.class);

        ClassFactory cf = newClassFactory(fab);

        expect( cf.newInterface( find("\\$FinalMethodBean_"))).andReturn(fab);

        replayAllRegisteredMocks();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        try
        {
            is.synthesizeInterface(FinalMethodBean.class);
            unreachable();
        }
        catch (ApplicationRuntimeException e)
        {
            assertEquals("Unable to create service for class 'org.ops4j.gaderian.service.impl.FinalMethodBean' due to method named 'finalMethod' being declared final",e.getMessage());
        }

        verifyAllRegisteredMocks();
    }

}