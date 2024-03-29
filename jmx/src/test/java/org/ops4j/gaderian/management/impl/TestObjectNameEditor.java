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

package org.ops4j.gaderian.management.impl;

import javax.management.ObjectName;

import org.ops4j.gaderian.management.impl.ObjectNameEditor;

import junit.framework.TestCase;

/**
 * Test of {@link org.ops4j.gaderian.management.impl.ObjectNameEditor}
 * 
 * @author Achim Huegen
 */
public class TestObjectNameEditor extends TestCase
{
    public void testSetAsText()
    {
        ObjectNameEditor editor = new ObjectNameEditor();
        editor.setAsText("Gaderian:name=test");
        ObjectName objectName = (ObjectName) editor.getValue();

        assertEquals("Gaderian:name=test", objectName.toString());
    }

    public void testMalformed()
    {
        ObjectNameEditor editor = new ObjectNameEditor();
        try
        {
            editor.setAsText("Gaderian=test:fail");
            fail();
        }
        catch (IllegalArgumentException ignore)
        {
        }
    }

    public void testGetAsText() throws Exception
    {
        ObjectNameEditor editor = new ObjectNameEditor();
        editor.setValue(new ObjectName("Gaderian:name=test"));

        assertEquals("Gaderian:name=test", editor.getAsText());
    }
}
