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

package org.ops4j.gaderian.servlet;

import java.io.IOException;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ShutdownCoordinator;
import org.ops4j.gaderian.events.RegistryShutdownListener;
import org.ops4j.gaderian.service.ThreadCleanupListener;
import org.ops4j.gaderian.service.ThreadEventNotifier;
import org.ops4j.gaderian.testutils.GaderianTestCase;

/**
 * Tests for {@link GaderianFilter}.
 *
 * @author Howard Lewis Ship
 */
public class TestGaderianFilter extends GaderianTestCase
{
    private static class ThreadListenerFixture implements ThreadCleanupListener
    {
        private boolean _cleanup;

        public void threadDidCleanup()
        {
            _cleanup = true;
        }

        public boolean getCleanup()
        {
            return _cleanup;
        }

    }

    private static class ShutdownListenerFixture implements RegistryShutdownListener
    {
        private boolean _didShutdown;

        public void registryDidShutdown()
        {
            _didShutdown = true;
        }

        public boolean getDidShutdown()
        {
            return _didShutdown;
        }

    }

    private static class FailingGaderianFilterFixture extends GaderianFilter
    {

        protected Registry constructRegistry(FilterConfig config)
        {
            throw new ApplicationRuntimeException("Forced failure.");
        }

    }

    private static class RegistryExposingGaderianFilterFixture extends GaderianFilter
    {

        private Registry _registry;

        public Registry getRegistry()
        {
            return _registry;
        }

        protected Registry constructRegistry(FilterConfig config)
        {
            _registry = super.constructRegistry(config);
            return _registry;
        }

    }

    private static class RebuildRegistryChainFixture implements FilterChain
    {
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
                ServletException
        {
            GaderianFilter.rebuildRegistry((HttpServletRequest) request);
        }
    }

    /** @since 1.1 */

    public void testLoadsFromWebInf() throws Exception
    {
        ServletContext context = createMock(ServletContext.class);

        FilterConfig config = createMock(FilterConfig.class);

        expect(config.getServletContext()).andReturn( context );

        URL url = getClass().getResource("webinf-hivemodule.xml");

        expect(context.getResource(GaderianFilter.HIVE_MODULE_XML)).andReturn( url ).times(2);

        replayAllRegisteredMocks();

        RegistryExposingGaderianFilterFixture f = new RegistryExposingGaderianFilterFixture();

        f.init(config);

        Registry r = f.getRegistry();

        assertEquals("was here", r.expandSymbols("${kilroy}", null));

        verifyAllRegisteredMocks();
    }

    public void testBasic() throws Exception
    {
        FilterConfig filterConfig = newFilterConfig();

        replayAllRegisteredMocks();

        RegistryExposingGaderianFilterFixture f = new RegistryExposingGaderianFilterFixture();

        f.init(filterConfig);

        verifyAllRegisteredMocks();

        Registry r = f.getRegistry();

        assertNotNull(r);

        ThreadEventNotifier t = r.getService(
                Gaderian.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        ThreadListenerFixture l = new ThreadListenerFixture();

        t.addThreadCleanupListener(l);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = (HttpServletResponse) createMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) createMock(FilterChain.class);

        request.setAttribute(GaderianFilter.REQUEST_KEY, r);

        chain.doFilter(request, response);

        expect(request.getAttribute(GaderianFilter.REBUILD_REQUEST_KEY)).andReturn( null );

        expect(request.getAttribute(GaderianFilter.REQUEST_KEY)).andReturn( r );

        replayAllRegisteredMocks();

        f.doFilter(request, response, chain);

        assertSame(r, GaderianFilter.getRegistry(request));

        assertEquals(true, l.getCleanup());

        f.destroy();

        try
        {
            t.addThreadCleanupListener(null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }

        verifyAllRegisteredMocks();
    }

    public void testShutdown() throws Exception
    {
        ServletContext context = createMock(ServletContext.class);
        FilterConfig filterConfig = createMock(FilterConfig.class);

        expect(filterConfig.getServletContext()).andReturn( context );

        expect(context.getResource(GaderianFilter.HIVE_MODULE_XML)).andReturn( null );

        replayAllRegisteredMocks();

        RegistryExposingGaderianFilterFixture f = new RegistryExposingGaderianFilterFixture();

        f.init(filterConfig);

        verifyAllRegisteredMocks();

        Registry r = f.getRegistry();

        assertNotNull(r);

        ShutdownCoordinator coordinator = (ShutdownCoordinator) r
                .getService(ShutdownCoordinator.class);

        ShutdownListenerFixture l = new ShutdownListenerFixture();

        coordinator.addRegistryShutdownListener(l);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = (HttpServletResponse) createMock(HttpServletResponse.class);
        FilterChain chain = new RebuildRegistryChainFixture();

        request.setAttribute(GaderianFilter.REQUEST_KEY, r);

        request.setAttribute(GaderianFilter.REBUILD_REQUEST_KEY, Boolean.TRUE);

        expect(request.getAttribute(GaderianFilter.REBUILD_REQUEST_KEY)).andReturn( Boolean.TRUE );

        expect(filterConfig.getServletContext()).andReturn( context );

        expect(context.getResource(GaderianFilter.HIVE_MODULE_XML)).andReturn( null );

        replayAllRegisteredMocks();

        f.doFilter(request, response, chain);

        verifyAllRegisteredMocks();

        assertEquals(true, l.getDidShutdown());
    }

    private FilterConfig newFilterConfig() throws Exception
    {
        ServletContext context = createMock(ServletContext.class);

        expect(context.getResource(GaderianFilter.HIVE_MODULE_XML)).andReturn( null );

        return newFilterConfig(context);
    }

    private FilterConfig newFilterConfig(ServletContext context)
    {
        FilterConfig config = createMock(FilterConfig.class);

        expect(config.getServletContext()).andReturn( context );

        return config;
    }

    public void testExceptionInInit() throws Exception
    {
        Filter f = new FailingGaderianFilterFixture();

        interceptLogging(GaderianFilter.class.getName());

        f.init(null);

        assertLoggedMessage("Forced failure");

        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = (HttpServletResponse) createMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) createMock(FilterChain.class);

        request.setAttribute(GaderianFilter.REQUEST_KEY, null);

        chain.doFilter(request, response);

        expect(request.getAttribute(GaderianFilter.REBUILD_REQUEST_KEY)).andReturn( null );

        replayAllRegisteredMocks();

        f.doFilter(request, response, chain);

        verifyAllRegisteredMocks();

        f.destroy();
    }

    public void testDestroyWithoutRepository()
    {
        Filter f = new GaderianFilter();

        f.destroy();
    }

    public void testFilterWithoutRepository() throws Exception
    {
        Filter f = new GaderianFilter();

        interceptLogging(GaderianFilter.class.getName());

        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = (HttpServletResponse) createMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) createMock(FilterChain.class);

        request.setAttribute(GaderianFilter.REQUEST_KEY, null);

        chain.doFilter(request, response);

        expect(request.getAttribute(GaderianFilter.REBUILD_REQUEST_KEY)).andReturn( null );

        replayAllRegisteredMocks();

        f.doFilter(request, response, chain);

        assertLoggedMessage("Unable to cleanup current thread");

        verifyAllRegisteredMocks();
    }

}