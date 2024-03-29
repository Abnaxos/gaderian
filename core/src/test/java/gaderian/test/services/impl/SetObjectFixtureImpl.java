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

import org.ops4j.gaderian.service.ClassFactory;

import gaderian.test.services.SetObjectFixture;

/**
 * Used to test the BuilderFactory's set-object element.
 *
 * @author Howard Lewis Ship
 */
public class SetObjectFixtureImpl implements SetObjectFixture
{

    private ClassFactory _classFactory1;
    private ClassFactory _classFactory2;

    public ClassFactory getClassFactory1()
    {
        return _classFactory1;
    }

    public ClassFactory getClassFactory2()
    {
        return _classFactory2;
    }

    public void setClassFactory1(ClassFactory factory)
    {
        _classFactory1 = factory;
    }

    public void setClassFactory2(ClassFactory factory)
    {
        _classFactory2 = factory;
    }

}
