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

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.events.RegistryShutdownListener;


/**
 * Used to test that singleton and deferred services properly registrer
 * core impls for registry shutdown.
 *
 * @author Howard Lewis Ship
 */
public class RunnableImpl implements Runnable, RegistryShutdownListener
{
	private Log _log;
	private String _type;
	
    public void run()
    {
		_log.debug("run -- " + _type);
    }

    public void registryDidShutdown()
    {
		_log.debug("registryDidShutdown -- " + _type);
    }

    public void setType(String string)
    {
        _type = string;
    }

    public void setLog(Log log)
    {
        _log = log;
    }

}
