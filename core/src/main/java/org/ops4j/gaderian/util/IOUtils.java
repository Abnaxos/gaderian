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

package org.ops4j.gaderian.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Tiny utilities used with InputStream and friends.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class IOUtils
{
    /**
     * Closes the input stream, if not null. Eats any IOException.
     */

    public static void close(InputStream stream)
    {
        if (stream != null)
            try
            {
                stream.close();
            }
            catch (IOException ex)
            {
                // Ignored.
            }
    }
}