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

package org.ops4j.gaderian.test;

import java.util.regex.Pattern;

import org.ops4j.gaderian.ApplicationRuntimeException;

/**
 * A {@link org.ops4j.gaderian.test.ArgumentMatcher} implementation that treats the expected
 * value (provided while training the mock object) as a Perl5 Regular expression, which is matched
 * against the actual value.
 * 
 * @author Howard Lewis Ship
 * @since 1.1
 */
public class RegexpMatcher extends AbstractArgumentMatcher
{

    public boolean compareArguments(Object expected, Object actual)
    {
        return matchRegexp((String) expected, (String) actual);
    }

    private boolean matchRegexp(String expectedRegexp, String actualString)
    {
        try
        {
            Pattern p = Pattern.compile(expectedRegexp);

            return p.matcher(actualString).matches();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }
    }
}