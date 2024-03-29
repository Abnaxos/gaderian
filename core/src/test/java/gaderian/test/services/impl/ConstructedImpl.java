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

import gaderian.test.services.Constructed;
import gaderian.test.services.SimpleService;

import org.ops4j.gaderian.service.ThreadLocalStorage;
import org.ops4j.gaderian.impl.BaseLocatable;

/**
 * Used for testing {@link org.ops4j.gaderian.schema.rules.ServiceTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class ConstructedImpl extends BaseLocatable implements Constructed
{
    private ThreadLocalStorage _threadLocal;
    private SimpleService _simpleService;

    public SimpleService getSimpleService()
    {
        return _simpleService;
    }

    public void setSimpleService(SimpleService service)
    {
        _simpleService = service;
    }

    public ThreadLocalStorage getThreadLocal()
    {
        return _threadLocal;
    }

    public void setThreadLocal(ThreadLocalStorage storage)
    {
        _threadLocal = storage;
    }

}
