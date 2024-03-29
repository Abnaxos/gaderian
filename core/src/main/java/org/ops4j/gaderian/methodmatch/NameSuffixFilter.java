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

package org.ops4j.gaderian.methodmatch;

import org.ops4j.gaderian.service.MethodSignature;

/**
 * Used to match a method name against a suffix.
 *
 * @author Howard Lewis Ship
 */
public class NameSuffixFilter extends MethodFilter
{
    private String _nameSuffix;

    public NameSuffixFilter(String nameSuffix)
    {
        _nameSuffix = nameSuffix;
    }

    public boolean matchMethod(MethodSignature sig)
    {
        return sig.getName().endsWith(_nameSuffix);
    }

}
