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

package org.ops4j.gaderian.impl;

import java.util.Locale;

import org.ops4j.gaderian.internal.MessageFinder;
import org.ops4j.gaderian.test.GaderianTestCase;
import org.ops4j.gaderian.util.ClasspathResource;

/**
 * @author Howard M. Lewis Ship
 */
public class TestMessageFinder extends GaderianTestCase
{
    private MessageFinder newFinder()
    {
        ClasspathResource r = new ClasspathResource(getClassResolver(),
                "org/ops4j/gaderian/impl/MessageFinder.xml");

        return new MessageFinderImpl(r);
    }

    public void testLocaleOverridesBase()
    {
        MessageFinder mf = newFinder();

        assertEquals("MessageFinder_fr.overriden-in-base", mf.getMessage(
                "overridden-in-base",
                Locale.FRENCH));

        // FRANCE is more detailed than FRENCH
        // Also, there is no MessageFinder_fr_FR.properties file,
        // and that's ok.

        assertEquals("MessageFinder_fr.overriden-in-base", mf.getMessage(
                "overridden-in-base",
                Locale.FRANCE));
    }

    public void testLocaleDoeNotObscureBase()
    {
        MessageFinder mf = newFinder();

        assertEquals("MessageFinder.only-in-properties", mf.getMessage(
                "only-in-base",
                Locale.FRENCH));
    }
}