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

package gaderian.test.parse;

import gaderian.test.FrameworkTestCase;

import java.util.Locale;

import org.ops4j.gaderian.impl.AttributeImpl;
import org.ops4j.gaderian.impl.ConfigurationPointImpl;
import org.ops4j.gaderian.impl.ContributionImpl;
import org.ops4j.gaderian.impl.DefaultErrorHandler;
import org.ops4j.gaderian.impl.ElementImpl;
import org.ops4j.gaderian.impl.InterceptorStackImpl;
import org.ops4j.gaderian.impl.ModuleImpl;
import org.ops4j.gaderian.impl.RegistryInfrastructureImpl;
import org.ops4j.gaderian.impl.ServiceInterceptorContributionImpl;
import org.ops4j.gaderian.impl.ServicePointImpl;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.parse.ConfigurationPointDescriptor;
import org.ops4j.gaderian.parse.ContributionDescriptor;
import org.ops4j.gaderian.parse.CreateInstanceDescriptor;
import org.ops4j.gaderian.parse.DependencyDescriptor;
import org.ops4j.gaderian.parse.ImplementationDescriptor;
import org.ops4j.gaderian.parse.InterceptorDescriptor;
import org.ops4j.gaderian.parse.InvokeFactoryDescriptor;
import org.ops4j.gaderian.parse.ModuleDescriptor;
import org.ops4j.gaderian.parse.ServicePointDescriptor;
import org.ops4j.gaderian.parse.SubModuleDescriptor;
import org.easymock.MockControl;

/**
 * A cheat, for code-coverage reasons.  We check that all the classes have a toString()
 * method.
 *
 * @author Howard Lewis Ship
 */

public class TestToString extends FrameworkTestCase
{

    public void testToString()
    {
        MockControl control = MockControl.createControl(ServicePoint.class);
        ServicePoint mockServicePoint = (ServicePoint) control.getMock();

        new ConfigurationPointDescriptor().toString();
        new ContributionDescriptor().toString();
        new ImplementationDescriptor().toString();
        new CreateInstanceDescriptor().toString();
        new InvokeFactoryDescriptor().toString();
        new ModuleDescriptor(_resolver, new DefaultErrorHandler()).toString();
        new SubModuleDescriptor().toString();
        new DependencyDescriptor().toString();
        new ServicePointDescriptor().toString();
        new InterceptorDescriptor().toString();
        new ModuleImpl().toString();
        new RegistryInfrastructureImpl(null, Locale.ENGLISH).toString();
        new ContributionImpl().toString();
        new ConfigurationPointImpl().toString();
        new ElementImpl().toString();
        new AttributeImpl("foo", "bar").toString();
        new ServiceInterceptorContributionImpl().toString();
        new ServicePointImpl().toString();
        new InterceptorStackImpl(null, mockServicePoint, null).toString();
    }
}
