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

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Location;

/**
 * An implementation of {@link org.ops4j.gaderian.ErrorHandler} that
 * throws an {@link org.ops4j.gaderian.ApplicationRuntimeException}
 * <em>instead of</em> logging an error.
 *
 * @author Howard Lewis Ship
 */
public class StrictErrorHandler implements ErrorHandler
{

    public void error(Log log, String message, Location location, Throwable cause)
    {
        String exceptionMessage =
            location == null
                ? ImplMessages.unlocatedError(message)
                : ImplMessages.locatedError(location, message);

        throw new ApplicationRuntimeException(exceptionMessage, location, cause);
    }

}
