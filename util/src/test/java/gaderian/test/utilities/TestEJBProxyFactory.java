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

package gaderian.test.utilities;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;
import javax.naming.Context;

import gaderian.test.utilities.impl.FakeContext;
import gaderian.test.utilities.impl.NameLookupHack;
import gaderian.test.utilities.impl.SimpleHomeImpl;
import static org.easymock.classextension.EasyMock.anyObject;
import static org.easymock.classextension.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.impl.ServiceImplementationFactoryParametersImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.service.ClassFactory;
import org.ops4j.gaderian.service.impl.ClassFactoryImpl;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.utilities.NameLookup;
import org.ops4j.gaderian.utilities.RemoteExceptionCoordinator;
import org.ops4j.gaderian.utilities.impl.EJBProxyFactory;
import org.ops4j.gaderian.utilities.impl.EJBProxyParameters;

/**
 * Tests for {@link org.ops4j.gaderian.utilities.impl.EJBProxyFactory}.
 *
 * @author Howard M. Lewis Ship
 */

public class TestEJBProxyFactory extends GaderianCoreTestCase
{
    protected void tearDown() throws Exception
    {
        super.tearDown();

        NameLookupHack._context = null;
        NameLookupHack._properties = null;
    }

    public void testEJBProxy() throws Exception
    {
        Registry r = buildFrameworkRegistry("EJBProxy.xml", false );

        SimpleHomeImpl home = new SimpleHomeImpl();
        FakeContext context = new FakeContext();
        context.bind("gaderian.test.utilities.Simple", home);
        NameLookupHack._context = context;

        SimpleRemote object = (SimpleRemote) r.getService(
                "gaderian.test.utilities.SimpleRemote",
                SimpleRemote.class);

        assertEquals(7, object.add(4, 3));
        // Exercise several code paths where objects are ready or cached.
        assertEquals(201, object.add(1, 200));

        // Tacked on here, a few tests that the NameLookup service builds
        // the initial context properties correctly.

        Map p = NameLookupHack._properties;

        assertEquals("fred", p.get(Context.INITIAL_CONTEXT_FACTORY));
        assertEquals("barney", p.get(Context.URL_PKG_PREFIXES));
        assertEquals("wilma", p.get(Context.PROVIDER_URL));
    }

    public void testEJBProxyNameFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("EJBProxy.xml", false );

        FakeContext context = new FakeContext();
        context.setForceError(true);

        NameLookupHack._context = context;

        SimpleRemote object = r.getService(
                "gaderian.test.utilities.SimpleRemote",
                SimpleRemote.class);

        try
        {

            object.add(4, 3);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to lookup 'gaderian.test.utilities.Simple' in JNDI context");

            Throwable t = findNestedException(ex);

            assertExceptionSubstring(t, "Forced error: gaderian.test.utilities.Simple");
        }
    }

    public void testEJBProxyRemoteFailure() throws Exception
    {
        Registry r = buildFrameworkRegistry("EJBProxy.xml", false );

        SimpleHomeImpl home = new SimpleHomeImpl();
        home.setForceError(true);

        FakeContext context = new FakeContext();
        context.bind("gaderian.test.utilities.Simple", home);
        NameLookupHack._context = context;

        NameLookupHack._context = context;

        SimpleRemote object = (SimpleRemote) r.getService(
                "gaderian.test.utilities.SimpleRemote",
                SimpleRemote.class);

        try
        {

            object.add(4, 3);
            unreachable();
        }
        catch (RemoteException ex)
        {
            assertExceptionSubstring(ex, "Forced error.");
        }
    }

    public void testEJBProxyRemoteFailureWithMocks() throws Exception
    {
        final EJBProxyFactory ejbProxyFactory = new EJBProxyFactory();

        final NameLookup nameLookup = createMock(NameLookup.class);
        final ClassFactory classFactory = new ClassFactoryImpl();
        final RemoteExceptionCoordinator remoteExceptionCoordinator = createMock(RemoteExceptionCoordinator.class);
        
        ejbProxyFactory.setClassFactory(classFactory);
        ejbProxyFactory.setNameLookup(nameLookup);
        ejbProxyFactory.setCoordinator(remoteExceptionCoordinator);
        
        final EJBProxyParameters ejbProxyParameters = new EJBProxyParameters();
        ejbProxyParameters.setHomeInterfaceClassName("gaderian.test.utilities.SimpleHome");
        ejbProxyParameters.setJndiName("jndi");

        final ServicePoint servicePoint = createMock(ServicePoint.class);
        final Module module = createMock(Module.class);

        expect(servicePoint.getServiceInterface()).andReturn(SimpleRemote.class);
        expect(module.resolveType("gaderian.test.utilities.SimpleHome")).andReturn(SimpleHome.class);
        expect(servicePoint.getExtensionPointId()).andReturn("fred");
        final SimpleHomeImpl value = new SimpleHomeImpl();
        value.setForceError(true);
        expect(nameLookup.lookup("jndi", Object.class)).andReturn(value).times(2);

        remoteExceptionCoordinator.fireRemoteExceptionDidOccur(anyObject(), (Throwable)anyObject());
        
        replayAllRegisteredMocks();

        try
        {
            final SimpleRemote object = (SimpleRemote) ejbProxyFactory.createCoreServiceImplementation(new ServiceImplementationFactoryParametersImpl(servicePoint, module, Arrays.asList(ejbProxyParameters)));
            object.add(1, 1);
        }
        catch (RemoteException ex)
        {
            assertExceptionSubstring(ex, "Forced error.");
        }


        verifyAllRegisteredMocks();
    }

    public void testEJBProxyNameFailureWithMocks()
    {
        
        final EJBProxyFactory ejbProxyFactory = new EJBProxyFactory();
        final NameLookup nameLookup = createMock(NameLookup.class);
        final ClassFactory classFactory = new ClassFactoryImpl();
        
        ejbProxyFactory.setClassFactory(classFactory);
        ejbProxyFactory.setNameLookup(nameLookup);

        final EJBProxyParameters ejbProxyParameters = new EJBProxyParameters();
        ejbProxyParameters.setHomeInterfaceClassName("gaderian.test.utilities.SimpleHome");
        ejbProxyParameters.setJndiName("jndi");

        final ServicePoint servicePoint = createMock(ServicePoint.class);
        final Module module = createMock(Module.class);
        
        expect(servicePoint.getServiceInterface()).andReturn(SimpleRemote.class);
        expect(module.resolveType("gaderian.test.utilities.SimpleHome")).andReturn(SimpleHome.class);
        expect(servicePoint.getExtensionPointId()).andReturn("fred");
        expect(nameLookup.lookup("jndi",Object.class)).andThrow(new ApplicationRuntimeException("Forced error: jndi"));
        
        replayAllRegisteredMocks();

        try
        {
            final SimpleRemote object = (SimpleRemote) ejbProxyFactory.createCoreServiceImplementation(new ServiceImplementationFactoryParametersImpl(servicePoint, module, Arrays.asList(ejbProxyParameters)));
            object.add(1,1);
        }
        catch (Exception ex)
        {
            assertExceptionSubstring(ex,"Forced error: jndi");
        }


        verifyAllRegisteredMocks();
        
    }

}