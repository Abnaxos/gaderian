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

package org.ops4j.gaderian.examples.impl;

import org.ops4j.gaderian.examples.Subtracter;

/**
 * Implementation of the {@link org.ops4j.gaderian.examples.Subtracter} interface.
 *
 * @author Howard Lewis Ship
 */
public class SubtracterImpl implements Subtracter
{

    public double subtract(double arg0, double arg1)
    {
        return arg0 - arg1;
    }

}
