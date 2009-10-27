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

package org.ops4j.gaderian.impl;

import org.ops4j.gaderian.Messages;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.test.GaderianTestCase;

/**
 * @author James Carman
 * @since 1.1
 */
public class TestRegistryImpl extends GaderianTestCase
{
    public void testGetModuleMessages() throws Exception
    {
        final Registry reg = buildFrameworkRegistry( "Privates.xml", false );
        final Messages msgs = reg.getModuleMessages( "gaderian.test.privates" );
        assertEquals( "Test Message", msgs.getMessage( "test.message" ) );
    }
}
