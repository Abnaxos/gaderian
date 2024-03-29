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

package org.ops4j.gaderian;

/**
 * A wrapper around {@link org.ops4j.gaderian.ErrorHandler} and
 * {@link org.apache.commons.logging.Log}for the most common case: reporting recoverable errors.
 * The point is that most services can use a single property of type ErrorLog, rather than a pair of
 * properties (ErrorHandler and Log).
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface ErrorLog
{
    /**
     * Invokes {@link ErrorHandler#error(Log, String, Location, Throwable)}.
     */

    public void error(String message, Location location, Throwable cause);
}