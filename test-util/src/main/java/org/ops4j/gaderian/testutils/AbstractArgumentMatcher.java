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

package org.ops4j.gaderian.testutils;

import org.easymock.AbstractMatcher;

/**
 * Base class that bridges from EasyMock's {@link org.easymock.AbstractMatcher} to Gaderian's
 * {@link org.ops4j.gaderian.test.ArgumentMatcher}. Since all the {@link ArgumentMatcher}
 * &nbsp;implementations extend this class, each can be used with {@link AbstractArgumentMatcher}
 * &nbsp;to compare a <em>single</em> argument, or on its own to match <em>all</em> arguments.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public abstract class AbstractArgumentMatcher extends AbstractMatcher implements ArgumentMatcher
{

    protected boolean argumentMatches(Object expected, Object actual)
    {
        return compareArguments(expected, actual);
    }
}