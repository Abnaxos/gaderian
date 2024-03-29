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

package org.ops4j.gaderian.strategy;

import org.ops4j.gaderian.impl.BaseLocatable;

/**
 * Provides a mapping of subject class to strategy instance, used by
 * {@link StrategyFactory} when building a service.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class StrategyContribution extends BaseLocatable
{
    private Class _registerClass;

    private Object _strategy;

    public Object getStrategy()
    {
        return _strategy;
    }

    public void setStrategy(Object adaptor)
    {
        _strategy = adaptor;
    }

    public Class getRegisterClass()
    {
        return _registerClass;
    }

    public void setRegisterClass(Class registerClass)
    {
        _registerClass = registerClass;
    }
}