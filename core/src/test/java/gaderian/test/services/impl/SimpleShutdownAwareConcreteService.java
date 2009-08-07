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

package gaderian.test.services.impl;

import org.ops4j.gaderian.events.RegistryShutdownListener;
import org.apache.commons.logging.Log;

/**
 * Basic service used for testing Gaderian.
 *
 * @author Howard Lewis Ship
 */
public class SimpleShutdownAwareConcreteService implements RegistryShutdownListener
{
    public Log _log;

    public void setLog(final Log log)
    {
        _log = log;
    }

    public int add(int a, int b)
    {
        return a+b;
    }

    public void registryDidShutdown()
    {
       _log.debug("registryDidShutdown --- SimpleShutdownAwareConcreteService"); 
    }
}
