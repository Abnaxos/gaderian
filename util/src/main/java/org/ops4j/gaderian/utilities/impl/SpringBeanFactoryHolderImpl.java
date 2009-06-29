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

package org.ops4j.gaderian.utilities.impl;

import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.utilities.SpringBeanFactoryHolder;
import org.springframework.beans.factory.BeanFactory;

/**
 * Simple holder of a {@link org.springframework.beans.factory.BeanFactory}.
 *
 * @author Howard Lewis Ship
 */
public class SpringBeanFactoryHolderImpl extends BaseLocatable implements SpringBeanFactoryHolder
{
    private BeanFactory _factory;

    public void setBeanFactory(BeanFactory factory)
    {
        _factory = factory;
    }

    public BeanFactory getBeanFactory()
    {
        return _factory;
    }

}
