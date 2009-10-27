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
import org.ops4j.gaderian.AssemblyParameters;
import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Messages;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.service.impl.AssemblyInstructionImpl;
import org.ops4j.gaderian.service.impl.BuilderClassResolverFacet;
import org.ops4j.gaderian.service.impl.BuilderErrorHandlerFacet;
import org.ops4j.gaderian.service.impl.BuilderErrorLogFacet;
import org.ops4j.gaderian.service.impl.BuilderFacet;
import org.ops4j.gaderian.service.impl.BuilderLogFacet;
import org.ops4j.gaderian.service.impl.BuilderMessagesFacet;
import org.ops4j.gaderian.service.impl.BuilderServiceIdFacet;
import org.ops4j.gaderian.test.GaderianTestCase;
import org.easymock.MockControl;

/**
 * Tests for the dependency injection logic of {@link AssemblyInstructionImpl} and various
 * implementations of {@link org.ops4j.gaderian.service.impl.BuilderFacet}.
 *
 * @author Knut Wannheden
 */
public class TestAssemblyInstruction extends GaderianTestCase
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

        replayControls();

        BuilderFacet f = new BuilderErrorHandlerFacet();

        Object actual = f.getFacetValue(p, null);

        assertSame(eh, actual);

        verifyControls();
    }

    public void testBuilderClassResolverFacet()
    {
        AssemblyParameters p = newParameters();
        Module m = newModule();
        ClassResolver cr = getClassResolver();

        trainGetInvokingModule(p, m);
        trainGetClassResolver(m, cr);

        replayControls();

        BuilderClassResolverFacet fc = new BuilderClassResolverFacet();

        Object result = fc.getFacetValue(p, null);

        assertSame(cr, result);

        verifyControls();
    }

    protected AssemblyParameters newParameters()
    {
        final MockControl control = MockControl.createNiceControl(AssemblyParameters.class);
        addControl(control);
        return (AssemblyParameters) control.getMock();
    }

    protected Module newModule()
    {
        final MockControl control = MockControl.createNiceControl(Module.class);
        addControl(control);
        return (Module) control.getMock();
    }

    protected ErrorHandler newErrorHandler()
    {
        return (ErrorHandler) newMock(ErrorHandler.class);
    }

    protected Log newLog()
    {
        return (Log) newMock(Log.class);
    }

    protected Messages newMessages()
    {
        return (Messages) newMock(Messages.class);
    }

    protected ErrorLog newErrorLog()
    {
        return (ErrorLog) newMock(ErrorLog.class);
    }

    public void testAutowire()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();
        ErrorHandler eh = newErrorHandler();
        Log log = newLog();
        Messages messages = newMessages();
        ErrorLog errorLog = newErrorLog();

        AutowireTarget t = (AutowireTarget) newMock(AutowireTarget.class);

        trainGetInvokingModule(fp, module);

        trainGetLog(fp, log);
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

        replayControls();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();

        p.addProperty(new BuilderLogFacet());
        p.addProperty(new BuilderClassResolverFacet());
        p.addProperty(new BuilderMessagesFacet());
        p.addProperty(new BuilderErrorHandlerFacet());
        p.addProperty(new BuilderServiceIdFacet());
        p.addProperty(new BuilderErrorLogFacet());

        p.assemble(t, fp);

        verifyControls();
    }

    private void trainGetErrorLog(AssemblyParameters fp, ErrorLog errorLog)
    {
        fp.getErrorLog();
        setReturnValue(fp, errorLog);
    }

    private void trainGetServiceId(AssemblyParameters fp)
    {
        fp.getServiceId();
        setReturnValue(fp, "foo.bar.Baz");
    }

    private void trainGetErrorHandler(Module module, ErrorHandler eh)
    {
        module.getErrorHandler();
        setReturnValue(module, eh);
    }

    private void trainGetMessages(Module module, Messages messages)
    {
        module.getMessages();
        setReturnValue(module, messages);
    }

    private void trainGetClassResolver(Module module, ClassResolver resolver)
    {
        module.getClassResolver();
        setReturnValue(module, resolver);
    }

    private void trainGetInvokingModule(AssemblyParameters fp, Module module)
    {
        fp.getInvokingModule();
        getControl(fp).setDefaultReturnValue(module);
    }

    protected void trainGetServiceId(AssemblyParameters fp, String serviceId)
    {
        fp.getServiceId();
        getControl(fp).setDefaultReturnValue(serviceId);
    }

    protected void trainGetLog(AssemblyParameters fp, Log log)
    {
        fp.getLog();
        getControl(fp).setDefaultReturnValue(log);
    }

    private void trainDebug(AssemblyParameters fp, Log log, String string)
    {
        fp.getLog();
        setReturnValue(fp, log);

        log.isDebugEnabled();
        setReturnValue(log, true);

        log.debug(string);
    }

    /**
     * Test that AssemblyInstructionImpl will invoke the "initializeService" method by default.
     */
    public void testAutowireInitializer()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();

        InitializeFixture f = (InitializeFixture) newMock(InitializeFixture.class);

        trainGetInvokingModule(fp, module);

        f.initializeService();

        replayControls();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();

        p.assemble(f, fp);

        verifyControls();
    }

    /**
     * Test that BuilderFactory will invoke the named initializer.
     */
    public void testInitializer()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();

        InitializeFixture f = (InitializeFixture) newMock(InitializeFixture.class);

        trainGetInvokingModule(fp, module);

        f.initializeCustom();

        replayControls();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();
        p.setInitializeMethod("initializeCustom");

        p.assemble(f, fp);

        verifyControls();
    }

    public void testAutowireServices()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        ServiceAutowireTarget f = (ServiceAutowireTarget) newMock(ServiceAutowireTarget.class);

        trainGetInvokingModule(fp, module);
        trainGetLog(fp, log);

        StringHolder h = new StringHolderImpl();

        trainContainsService(module, String.class, false);
        trainContainsService(module, StringHolder.class, true);
        trainGetService(module, StringHolder.class, h);
        f.setStringHolder(h);
        trainIsDebugEnabled(log);

        replayControls();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();
        p.setAutowireServices(true);

        p.assemble(f, fp);

        verifyControls();
    }

    private void trainIsDebugEnabled(Log log)
    {
        log.isDebugEnabled();
        setReturnValue(log, false);
    }

    private void trainGetService(Module module, Class serviceInterface, Object service)
    {
        module.getService(serviceInterface);
        setReturnValue(module, service);
    }

    private void trainContainsService(Module module, Class serviceInterface, boolean containsService)
    {
        module.containsService(serviceInterface);
        setReturnValue(module, containsService);
    }

    public void testAutowireServicesFailure()
    {
        AssemblyParameters fp = newParameters();
        Module module = newModule();
        Log log = newLog();

        ServiceAutowireTarget f = (ServiceAutowireTarget) newMock(ServiceAutowireTarget.class);

        trainGetInvokingModule(fp, module);
        trainGetLog(fp, log);

        trainContainsService(module, String.class, false);
        trainContainsService(module, StringHolder.class, false);

        replayControls();

        AssemblyInstructionImpl p = new AssemblyInstructionImpl();
        p.setAutowireServices(true);

        p.assemble(f, fp);

        verifyControls();
    }
}
