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

package org.ops4j.gaderian.order;

import java.util.List;

import gaderian.test.FrameworkTestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.easymock.EasyMock.*;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.impl.DefaultErrorHandler;

/**
 * Tests for the {@link org.ops4j.gaderian.order.Orderer}.
 * 
 * @author Howard Lewis Ship
 */
public class TestOrderer extends FrameworkTestCase
{
    private static final Log LOG = LogFactory.getLog(TestOrderer.class);

    private ErrorHandler getErrorHandler()
    {
        return createMock(ErrorHandler.class);
    }

    public void testNoDependencies() throws Exception
    {
        Orderer o = new Orderer(getErrorHandler(), "cartoon character");

        o.add("FRED", "fred", null, null);
        o.add("BARNEY", "barney", null, null);
        o.add("WILMA", "wilma", null, null);
        o.add("BETTY", "betty", null, null);

        replayAllRegisteredMocks();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "FRED", "BARNEY", "WILMA", "BETTY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testPrereq() throws Exception
    {
        Orderer o = new Orderer(getErrorHandler(), "cartoon character");

        o.add("FRED", "fred", "wilma", null);
        o.add("BARNEY", "barney", "betty", null);
        o.add("BETTY", "betty", null, null);
        o.add("WILMA", "wilma", null, null);

        replayAllRegisteredMocks();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "WILMA", "FRED", "BETTY", "BARNEY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testPostreq() throws Exception
    {
        Orderer o = new Orderer(getErrorHandler(), "cartoon character");

        o.add("FRED", "fred", null, "barney,wilma");
        o.add("BARNEY", "barney", null, "betty");
        o.add("BETTY", "betty", null, null);
        o.add("WILMA", "wilma", null, null);

        replayAllRegisteredMocks();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "FRED", "BARNEY", "BETTY", "WILMA" }, l);

        verifyAllRegisteredMocks();
    }

    public void testPrePostreq() throws Exception
    {
        Orderer o = new Orderer(getErrorHandler(), "cartoon character");

        o.add("FRED", "fred", null, "barney,wilma");
        o.add("BARNEY", "barney", "wilma", "betty");
        o.add("BETTY", "betty", null, null);
        o.add("WILMA", "wilma", null, null);

        replayAllRegisteredMocks();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "FRED", "WILMA", "BARNEY", "BETTY" }, l);

        verify();
    }

    public void testUnknownPrereq() throws Exception
    {
        ErrorHandler eh =  createMock(ErrorHandler.class);

        eh.error(LOG, "Unknown cartoon character dependency 'charlie' (for 'fred').", null, null);

        replayAllRegisteredMocks();

        Orderer o = new Orderer(LOG, eh, "cartoon character");

        o.add("FRED", "fred", "charlie", "barney,wilma");
        o.add("BARNEY", "barney", "wilma", "betty");
        o.add("BETTY", "betty", null, null);
        o.add("WILMA", "wilma", null, null);

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "FRED", "WILMA", "BARNEY", "BETTY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testUnknownPostreq() throws Exception
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        eh.error(LOG, "Unknown cartoon character dependency 'dino' (for 'betty').", null, null);

        replayAllRegisteredMocks();

        Orderer o = new Orderer(LOG, eh, "cartoon character");

        o.add("FRED", "fred", null, "barney,wilma");
        o.add("BARNEY", "barney", "wilma", "betty");
        o.add("BETTY", "betty", null, "dino");
        o.add("WILMA", "wilma", null, null);

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "FRED", "WILMA", "BARNEY", "BETTY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testCyclePre() throws Exception
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        eh.error(eq(LOG),eq(
                "Unable to order cartoon character 'wilma' due to dependency cycle:"
                        + " A cycle has been detected from the initial object [wilma]"),
                (Location)eq(null),
                isA(ApplicationRuntimeException.class));

        replayAllRegisteredMocks();

        Orderer o = new Orderer(LOG, eh, "cartoon character");

        o.add("FRED", "fred", "wilma", null);
        o.add("BARNEY", "barney", "betty", null);
        o.add("BETTY", "betty", "fred", null);
        o.add("WILMA", "wilma", "barney", null);

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "WILMA", "FRED", "BETTY", "BARNEY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testCyclePost() throws Exception
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        eh
                .error( eq(LOG),
                        eq("Unable to order cartoon character 'betty' due to dependency cycle: A cycle has been detected from the initial object [fred]"),
                        (Location)eq(null),
                        isA(ApplicationRuntimeException.class));


        replayAllRegisteredMocks();

        Orderer o = new Orderer(LOG, eh, "cartoon character");

        o.add("WILMA", "wilma", null, "betty");
        o.add("FRED", "fred", null, "barney");
        o.add("BARNEY", "barney", null, "wilma");
        o.add("BETTY", "betty", null, "fred");

        List l = o.getOrderedObjects();
        assertListsEqual(new Object[]
        { "FRED", "BARNEY", "WILMA", "BETTY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testDupe() throws Exception
    {
        Orderer o = new Orderer(new DefaultErrorHandler(), "cartoon character");

        o.add("FRED", "flintstone", null, null);
        o.add("BARNEY", "rubble", null, null);

        interceptLogging();

        o.add("WILMA", "flintstone", null, null);

        assertLoggedMessage("Cartoon character 'flintstone' duplicates previous value (at unknown location) and is being ignored.");

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "FRED", "BARNEY" }, l);
    }

    public void testPreStar() throws Exception
    {
        Orderer o = new Orderer(getErrorHandler(), "cartoon character");

        o.add("FRED", "fred", "*", null);
        o.add("BARNEY", "barney", "betty", null);
        o.add("WILMA", "wilma", "betty", null);
        o.add("BETTY", "betty", null, null);

        replayAllRegisteredMocks();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "BETTY", "BARNEY", "WILMA", "FRED" }, l);

        verifyAllRegisteredMocks();
    }

    public void testPreStartDupe() throws Exception
    {
        Orderer o = new Orderer(new DefaultErrorHandler(), "cartoon character");

        o.add("FRED", "fred", "*", null);
        o.add("BARNEY", "barney", "*", null);
        o.add("WILMA", "wilma", "betty", null);
        o.add("BETTY", "betty", null, null);

        interceptLogging();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "BARNEY", "BETTY", "WILMA", "FRED" }, l);

        assertLoggedMessage("Cartoon character 'barney' has been ordered "
                + "last, conflicting with 'fred' (at unknown location).");
    }

    public void testPostStar() throws Exception
    {
        Orderer o = new Orderer(getErrorHandler(), "cartoon character");

        o.add("FRED", "fred", null, "wilma");
        o.add("BARNEY", "barney", null, "*");
        o.add("WILMA", "wilma", null, "betty");
        o.add("BETTY", "betty", null, null);

        replayAllRegisteredMocks();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "BARNEY", "FRED", "WILMA", "BETTY" }, l);

        verifyAllRegisteredMocks();
    }

    public void testPostStarDupe() throws Exception
    {
        Orderer o = new Orderer(new DefaultErrorHandler(), "cartoon character");

        o.add("FRED", "fred", null, "wilma");
        o.add("BARNEY", "barney", null, "*");
        o.add("WILMA", "wilma", null, "*");
        o.add("BETTY", "betty", null, null);

        interceptLogging();

        List l = o.getOrderedObjects();

        assertListsEqual(new Object[]
        { "BARNEY", "FRED", "WILMA", "BETTY" }, l);

        assertLoggedMessage("Cartoon character 'wilma' has been ordered "
                + "first, conflicting with 'barney' (at unknown location).");
    }

    public void testNoObjects() throws Exception
    {
        Orderer o = new Orderer(new DefaultErrorHandler(), "cartoon character");

        List l = o.getOrderedObjects();

        assertEquals(0, l.size());
    }

    public void testException() throws Exception
    {
            String msg = OrdererMessages.exception("cartoon character", new NullPointerException(
                    "Unknown character exception"));
            assertEquals(
                    "Improper message returned from exception orderer",
                    "Unable to order cartoon characters: Unknown character exception",
                    msg);
    }
}