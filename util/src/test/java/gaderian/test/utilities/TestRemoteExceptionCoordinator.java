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

import org.ops4j.gaderian.utilities.RemoteExceptionEvent;
import org.ops4j.gaderian.test.GaderianTestCase;

public class TestRemoteExceptionCoordinator extends GaderianTestCase
{
    public void testRemoteExceptionEvent()
    {
        Throwable t = new RuntimeException();
        RemoteExceptionEvent ev = new RemoteExceptionEvent(this, t);

        assertSame(this, ev.getSource());
        assertSame(t, ev.getException());
    }
}
