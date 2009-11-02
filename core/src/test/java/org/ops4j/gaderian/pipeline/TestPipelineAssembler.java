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

package org.ops4j.gaderian.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.impl.DefaultImplementationBuilderImpl;
import org.ops4j.gaderian.service.ClassFactory;
import org.ops4j.gaderian.service.impl.ClassFactoryImpl;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for the {@link org.ops4j.gaderian.pipeline.PipelineAssembler} and
 * {@link org.ops4j.gaderian.pipeline.PipelineFactory} classes.
 *
 * @author Howard Lewis Ship
 */
public class TestPipelineAssembler extends GaderianCoreTestCase
{
    private static class StandardInner implements StandardService
    {
        private String _desciption;

        private StandardInner(String description)
        {
            _desciption = description;
        }

        public String toString()
        {
            return _desciption;
        }

        public int run(int i)
        {
            return i;
        }
    }

    public void testTerminatorConflict()
    {
        ErrorLog log = newErrorLog();

        log.error(
                "Terminator ss2 for pipeline service foo.bar conflicts with "
                        + "previous terminator (ss1, at unknown location) and has been ignored.",
                null,
                null);

        replayAllRegisteredMocks();

        PipelineAssembler pa = new PipelineAssembler(log, "foo.bar", StandardService.class,
                StandardFilter.class, null, null);

        StandardService ss1 = new StandardInner("ss1");
        StandardService ss2 = new StandardInner("ss2");

        pa.setTerminator(ss1, null);
        pa.setTerminator(ss2, null);

        assertSame(ss1, pa.getTerminator());

        verifyAllRegisteredMocks();
    }

    public void testIncorrectTerminatorType()
    {
        ErrorLog log = newErrorLog();

        log.error("-String- is not an instance of interface "
                + "org.ops4j.gaderian.pipeline.StandardService suitable for "
                + "use as part of the pipeline for service foo.bar.", null, null);

        replayAllRegisteredMocks();

        PipelineAssembler pa = new PipelineAssembler(log, "foo.bar", StandardService.class,
                StandardFilter.class, null, null);

        pa.setTerminator("-String-", null);

        assertNull(pa.getTerminator());

        verifyAllRegisteredMocks();
    }

    public void testIncorrectFilterType()
    {
        ErrorLog log = newErrorLog();

        log.error("-String- is not an instance of interface "
                + "org.ops4j.gaderian.pipeline.StandardFilter suitable for "
                + "use as part of the pipeline for service foo.bar.", null, null);

        replayAllRegisteredMocks();

        PipelineAssembler pa = new PipelineAssembler(log, "foo.bar", StandardService.class,
                StandardFilter.class, null, null);

        pa.addFilter("filter-name", null, null, "-String-", null);

        verifyAllRegisteredMocks();
    }

    public void testPassThruToPlaceholder()
    {
        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        ErrorLog log = newErrorLog();

        replayAllRegisteredMocks();

        PipelineAssembler pa = new PipelineAssembler(log, "foo.bar", StandardService.class,
                StandardFilter.class, new ClassFactoryImpl(), dib);

        StandardService pipeline = (StandardService) pa.createPipeline();

        assertEquals(0, pipeline.run(99));

        verifyAllRegisteredMocks();
    }

    public void testFilterChain()
    {
        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        ErrorLog log = newErrorLog();

        PipelineAssembler pa = new PipelineAssembler(log, "foo.bar", StandardService.class,
                StandardFilter.class, new ClassFactoryImpl(), dib);

        replayAllRegisteredMocks();

        pa.setTerminator(new StandardInner("ss"), null);

        StandardFilter adder = new StandardFilter()
        {
            public int run(int i, StandardService service)
            {
                return service.run(i + 3);
            }
        };

        StandardFilter multiplier = new StandardFilter()
        {
            public int run(int i, StandardService service)
            {
                return 2 * service.run(i);
            }
        };

        StandardFilter subtracter = new StandardFilter()
        {
            public int run(int i, StandardService service)
            {
                return service.run(i) - 2;
            }
        };

        pa.addFilter("subtracter", null, "adder", subtracter, null);
        pa.addFilter("adder", "multiplier", null, adder, null);
        pa.addFilter("multiplier", null, null, multiplier, null);

        StandardService pipeline = (StandardService) pa.createPipeline();

        // Should be order subtracter, multipler, adder
        assertEquals(14, pipeline.run(5));
        assertEquals(24, pipeline.run(10));

        verifyAllRegisteredMocks();
    }

    public void testPipelineFactoryWithTerminator()
    {
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        PipelineFactory factory = new PipelineFactory();
        factory.setClassFactory(cf);
        factory.setDefaultImplementationBuilder(dib);
        factory.setErrorLog(newErrorLog());

        PipelineParameters pp = new PipelineParameters();
        pp.setFilterInterface(StandardFilter.class);
        pp.setTerminator(new StandardInner("terminator"));

        List<FilterContribution> l = new ArrayList<FilterContribution>();

        FilterContribution fc = new FilterContribution();
        fc.setFilter(new StandardFilterImpl());
        fc.setName("multiplier-filter");

        l.add(fc);

        pp.setPipelineConfiguration(l);

        expect(fp.getParameters()).andReturn(Collections.singletonList(pp));

        expect(fp.getServiceId()).andReturn("example");

        expect(fp.getServiceInterface()).andReturn(StandardService.class);

        replayAllRegisteredMocks();

        StandardService s = (StandardService) factory.createCoreServiceImplementation(fp);

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));

        verifyAllRegisteredMocks();
    }

    public void testPipelineFactoryNoTerminator()
    {
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        ClassFactory cf = new ClassFactoryImpl();
        DefaultImplementationBuilderImpl dib = new DefaultImplementationBuilderImpl();

        dib.setClassFactory(cf);

        PipelineFactory factory = new PipelineFactory();
        factory.setClassFactory(cf);
        factory.setDefaultImplementationBuilder(dib);
        factory.setErrorLog(newErrorLog());

        PipelineParameters pp = new PipelineParameters();
        pp.setFilterInterface(StandardFilter.class);

        List<BaseLocatable> l = new ArrayList<BaseLocatable>();

        FilterContribution fc = new FilterContribution();
        fc.setFilter(new StandardFilterImpl());
        fc.setName("multiplier-filter");

        l.add(fc);

        TerminatorContribution tc = new TerminatorContribution();
        tc.setTerminator(new StandardServiceImpl());

        l.add(tc);

        pp.setPipelineConfiguration(l);

        expect(fp.getParameters()).andReturn(Collections.singletonList(pp));

        expect(fp.getServiceId()).andReturn("example");

        expect(fp.getServiceInterface()).andReturn(StandardService.class);

        replayAllRegisteredMocks();

        StandardService s = (StandardService) factory.createCoreServiceImplementation(fp);

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));

        verifyAllRegisteredMocks();
    }

    private ErrorLog newErrorLog()
    {
        return createMock(ErrorLog.class);
    }

    /**
     * Try it integrated now!
     */
    public void testFactoryWithServices() throws Exception
    {
        Registry r = buildFrameworkRegistry("Pipeline.xml", false );

        StandardService s = (StandardService) r.getService(
                "gaderian.test.Pipeline",
                StandardService.class);

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));
    }

    public void testFactoryWithObjects() throws Exception
    {
        Registry r = buildFrameworkRegistry("Pipeline.xml", false );

        StandardService s = (StandardService) r.getService(
                "gaderian.test.ObjectPipeline",
                StandardService.class);

        assertEquals(24, s.run(12));
        assertEquals(18, s.run(9));
    }
}