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

package org.ops4j.gaderian.examples.panorama.startup.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import static org.easymock.EasyMock.*;
import org.ops4j.gaderian.*;
import org.ops4j.gaderian.examples.panorama.startup.Executable;
import org.ops4j.gaderian.impl.DefaultClassResolver;
import org.ops4j.gaderian.impl.MessageFinderImpl;
import org.ops4j.gaderian.impl.ModuleMessages;
import org.ops4j.gaderian.internal.MessageFinder;
import org.ops4j.gaderian.service.ThreadLocale;
import org.ops4j.gaderian.service.impl.ThreadLocaleImpl;
import org.ops4j.gaderian.testutils.GaderianTestCase;
import org.ops4j.gaderian.util.ClasspathResource;


/**
 * Tests for the {@link org.ops4j.gaderian.examples.panorama.startup.impl.TaskExecutor} service.
 *
 * @author Howard Lewis Ship
 */
public class TestTaskExecutor extends GaderianTestCase
{
    private static List _tokens = new ArrayList();

    protected void setUp() throws Exception
    {
        super.setUp();

        _tokens.clear();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        _tokens.clear();
    }

    public static void addToken(String token)
    {
        _tokens.add(token);
    }

    public Messages getMessages()
    {
        Resource r = new ClasspathResource(new DefaultClassResolver(), "/META-INF/org/ops4j/gaderian/panorama.startup.xml");
        MessageFinder mf = new MessageFinderImpl(r);
        ThreadLocale tl = new ThreadLocaleImpl(Locale.getDefault());
        return new ModuleMessages(mf, tl);
    }

    public void testSuccess()
    {
        ExecutableFixture f1 = new ExecutableFixture("f1");

        Task t1 = new Task();

        t1.setExecutable(f1);
        t1.setId("first");
        t1.setAfter("second");
        t1.setTitle("Fixture #1");

        ExecutableFixture f2 = new ExecutableFixture("f2");

        Task t2 = new Task();
        t2.setExecutable(f2);
        t2.setId("second");
        t2.setTitle("Fixture #2");

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(t1);
        tasks.add(t2);

        Log log = createMock(Log.class);

        TaskExecutor e = new TaskExecutor();

        ErrorLog errorLog = createMock(ErrorLog.class);

        e.setErrorLog(errorLog);
        e.setLog(log);

        e.setMessages(getMessages());

        e.setTasks(tasks);

        // Note the ordering; explicitly set, to check that ordering does
        // take place.
        log.info(find("Executing task Fixture #2."));

        log.info(find("Executing task Fixture #1."));
        log.info(find("Executed 2 tasks \\(in \\d+ milliseconds\\)\\."));

        replayAllRegisteredMocks();
        e.run();

        assertListsEqual(new String[]
                {"f2", "f1"}, _tokens);

        verifyAllRegisteredMocks();


    }

    public void testFailure()
    {
        Executable f = new Executable()
        {
            public void execute() throws Exception
            {
                throw new ApplicationRuntimeException("Failure!");
            }
        };

        Task t = new Task();

        t.setExecutable(f);
        t.setId("failure");
        t.setTitle("Failure");

        List tasks = Collections.singletonList(t);

        Log log = createMock(Log.class);

        ErrorLog errorLog = createMock(ErrorLog.class);

        log.info("Executing task Failure.");

        errorLog.error(eq("Exception while executing task Failure: Failure!"),
                ( Location )eq(null),
                isA(ApplicationRuntimeException.class));

        log.info(find("Executed one task with one failure \\(in \\d+ milliseconds\\)\\."));

        replayAllRegisteredMocks();

        TaskExecutor e = new TaskExecutor();

        e.setErrorLog(errorLog);
        e.setLog(log);
        e.setMessages(getMessages());
        e.setTasks(tasks);

        e.run();

        verifyAllRegisteredMocks();
    }
}