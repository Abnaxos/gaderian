// Copyright 2006 The Apache Software Foundation
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

package gaderian.test.services;

import gaderian.test.services.impl.StringHolderImpl;
import org.apache.commons.logging.Log;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import org.ops4j.gaderian.*;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.service.impl.*;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for the dependency injection logic of {@link AssemblyInstructionImpl} and various
 * implementations of {@link org.ops4j.gaderian.service.impl.BuilderFacet}.
 *
 * @author Knut Wannheden
 */
public class TestAssemblyInstruction extends GaderianCoreTestCase
{
    public void testPropertyInjection() throws Exception
    {
        Registry r = buildFrameworkRegistry("AssemblyInstructions.xml", false );

        SimpleService s = (SimpleService) r.getService(
                "gaderian.test.services.SimpleAssembly",
                SimpleService.class);

        assertEquals(99, s.add(1, 1));
    }

    public void testEventListener() throws Exception
    {
        Registry r = buildFrameworkRegistry("AssemblyInstructions.xml", false );

        ZapEventProducer p = (ZapEventProducer) r.getService(
                "gaderian.test.services.ZapEventProducer",
                ZapEventProducer.class);

        ZapEventConsumer c = (ZapEventConsumer) r.getService(
                "gaderian.test.services.ZapEventConsumerAssembly",
                ZapEventConsumer.class);

        assertEquals(false, c.getDidZapWiggle());

        p.fireZapDidWiggle(null);

        assertEquals(true, c.getDidZapWiggle());
    }

    public void testBuilderErrorHandlerFacet()
    {
        AssemblyParameters p = newParameters();
        Module m = newModule();
        ErrorHandler eh = newErrorHandler();

        trainGetInvokingModule(p, m);
        trainGetErrorHandler(m, eh);

        replayAllRegisteredMocks();

        BuilderFacet f = new BuilderErrorHandlerFacet();

        Object actual = f.getFacetValue(p, null);

        assertSame(eh, actual);

        verifyAllRegisteredMocks();
    }

    public void testBuilderClassResolverFacet()
    {
        AssemblyParameters p = newParameters();
        Module m = newModule();
        ClassResolver cr = getClassResolver();

        trainGetInvokingModule(p, m);
        trainGetClassResolver(m, cr);

        replayAllRegisteredMocks();

        BuilderClassResolverFacet fc = new BuilderClassResolverFacet();

        Object result = fc.getFacetValue(p, null);

        assertSame(cr, result);

        verifyAllRegisteredMocks();
    }

    protected AssemblyParameters newParameters()
    {
        return createMock(AssemblyParameters.class);
    }

    protected Module newModule()
    {
        return createMock(Module.class);

    }

    protected ErrorHandler newErrorHandler()
    {
        return createMock(ErrorHandler.class);
    }

    protected Log newLog()
    {
        return createMock(Log.class);
    }

    protected Messages newMessages()
    {
        return createMock(Messages.class);
    }

    protected ErrorLog newErrorLog()
    {
        return createMock(ErrorLog.class);
    }

    public void testAutowire()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();
        ErrorHandler eh = newErrorHandler();
        Log log = newLog();
        Messages messages = newMessages();
        ErrorLog errorLog = newErrorLog();

        AutowireTarget t = createMock(AutowireTarget.class);

        trainGetInvokingModule(fp, module);

        trainGetLog(fp, log, 1 );
        t.setLog(log);
        trainDebug(fp, log, "Autowired property log to " + log);

        trainGetClassResolver(module, getClassResolver());
        t.setClassResolver(getClassResolver());
        trainDebug(fp, log, "Autowired property classResolver to " + getClassResolver());

        trainGetMessages(module, messages);
        t.setMessages(messages);
        trainDebug(fp, log, "Autowired property messages to " + messages);

        trainGetErrorHandler(module, eh);
        t.setErrorHandler(eh);
        trainDebug(fp, log, "Autowired property errorHandler to " + eh);

        trainGetServiceId(fp);
        t.setServiceId("foo.bar.Baz");
        trainDebug(fp, log, "Autowired property serviceId to foo.bar.Baz");

        trainGetErrorLog(fp, errorLog);
        t.setErrorLog(errorLog);
        trainDebug(fp, log, "Autowired property errorLog to " + errorLog);

        replayAllRegisteredMocks();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();

        p.addProperty(new BuilderLogFacet());
        p.addProperty(new BuilderClassResolverFacet());
        p.addProperty(new BuilderMessagesFacet());
        p.addProperty(new BuilderErrorHandlerFacet());
        p.addProperty(new BuilderServiceIdFacet());
        p.addProperty(new BuilderErrorLogFacet());

        p.assemble(t, fp);

        verifyAllRegisteredMocks();
    }

    private void trainGetErrorLog(AssemblyParameters fp, ErrorLog errorLog)
    {
        expect(fp.getErrorLog()).andReturn(errorLog);
    }

    private void trainGetServiceId(AssemblyParameters fp)
    {
        expect(fp.getServiceId()).andReturn("foo.bar.Baz");
    }

    private void trainGetErrorHandler(Module module, ErrorHandler eh)
    {
        expect(module.getErrorHandler()).andReturn(eh);
    }

    private void trainGetMessages(Module module, Messages messages)
    {
        expect(module.getMessages()).andReturn(messages);
    }

    private void trainGetClassResolver(Module module, ClassResolver resolver)
    {
        expect(module.getClassResolver()).andReturn(resolver);
    }

    private void trainGetInvokingModule(AssemblyParameters fp, Module module)
    {
        expect(fp.getInvokingModule()).andReturn(module).atLeastOnce();
    }

    protected void trainGetServiceId(AssemblyParameters fp, String serviceId)
    {
        expect(fp.getServiceId()).andReturn( serviceId );
    }

    protected void trainGetLog( AssemblyParameters fp, Log log, final int times )
    {
        expect(fp.getLog()).andReturn( log ).times( times );
    }

    private void trainDebug(AssemblyParameters fp, Log log, String string)
    {
        expect(fp.getLog()).andReturn( log );

        expect(log.isDebugEnabled()).andReturn( true);

        log.debug(string);
    }

    /**
     * Test that AssemblyInstructionImpl will invoke the "initializeService" method by default.
     */
    public void testAutowireInitializer()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();

        InitializeFixture f = createMock(InitializeFixture.class);

        //trainGetInvokingModule(fp, module);

        f.initializeService();

        replayAllRegisteredMocks();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();

        p.assemble(f, fp);

        verifyAllRegisteredMocks();
    }

    /**
     * Test that BuilderFactory will invoke the named initializer.
     */
    public void testInitializer()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();

        InitializeFixture f = createMock(InitializeFixture.class);

        // trainGetInvokingModule(fp, module);

        f.initializeCustom();

        replayAllRegisteredMocks();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();
        p.setInitializeMethod("initializeCustom");

        p.assemble(f, fp);

        verifyAllRegisteredMocks();
    }

    public void testAutowireServices()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        ServiceAutowireTarget f = createMock(ServiceAutowireTarget.class);

        trainGetInvokingModule(fp, module);
        trainGetLog(fp, log, 3 );

        StringHolder h = new StringHolderImpl();

        trainContainsService(module, String.class, false);
        trainContainsService(module, StringHolder.class, true);
        trainContainsService(module, isA( Class.class ), false);
        trainGetService(module, StringHolder.class, h);
        f.setStringHolder(h);
        trainIsDebugEnabled(log);

        replayAllRegisteredMocks();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();
        p.setAutowireServices(true);

        p.assemble(f, fp);

        verifyAllRegisteredMocks();
    }

    private void trainIsDebugEnabled(Log log)
    {
        expect(log.isDebugEnabled()).andReturn(false);
    }

    private void trainGetService(Module module, Class serviceInterface, Object service)
    {
        expect(module.getService(serviceInterface)).andReturn(service);
    }

    private void trainContainsService(Module module, Class serviceInterface, boolean containsService)
    {
        expect(module.containsService(serviceInterface)).andReturn(containsService);
    }

    public void testAutowireServicesFailure()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        ServiceAutowireTarget f = createMock(ServiceAutowireTarget.class);

        trainGetInvokingModule(fp, module);
        trainGetLog(fp, log, 3 );

        trainContainsService(module, String.class, false);
        trainContainsService(module, StringHolder.class, false);
        trainContainsService(module, isA( Class.class ), false);

        replayAllRegisteredMocks();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();
        p.setAutowireServices(true);

        p.assemble(f, fp);

        verifyAllRegisteredMocks();
    }
}
