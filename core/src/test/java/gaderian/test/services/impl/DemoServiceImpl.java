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

import gaderian.test.services.DemoService;

import org.ops4j.gaderian.ApplicationRuntimeException;

/**
 * Implementation of {@link gaderian.test.services.ITestService} used
 * for testing logging interceptor.
 *
 * @author Howard Lewis Ship
 */
public class DemoServiceImpl extends SimpleServiceImpl implements DemoService
{

    /**
     * Does nothing.
     */
    public void noResult()
    {

    }

    /**
     * Throws ApplicationRuntimeException.
     */
    public void alwaysFail() throws RuntimeException
    {
        throw new ApplicationRuntimeException("Failure in method alwaysFail.", (Throwable) null);
    }

}
