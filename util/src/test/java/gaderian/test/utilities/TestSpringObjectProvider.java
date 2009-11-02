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

package gaderian.test.utilities;

import org.easymock.EasyMock;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.utilities.SpringBeanFactoryHolder;
import org.ops4j.gaderian.utilities.impl.SpringObjectProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestSpringObjectProvider extends GaderianCoreTestCase
{
    public void testBasic()
    {
        Object bean = new Object();
        BeanFactory bf = createMock(BeanFactory.class);

        EasyMock.expect(bf.getBean("fred")).andReturn(bean);

        replayAllRegisteredMocks();

        SpringObjectProvider p = new SpringObjectProvider();
        p.setBeanFactory(bf);

        Object result = p.provideObject(null, null, "fred", null);

        assertSame(bean, result);

        verifyAllRegisteredMocks();
    }

    public void testSpringIntegration() throws Exception
    {
        // Spring setup

        ClassPathResource springBeansResource = new ClassPathResource("SpringBeans.xml",
                TestSpringLookupFactory.class);

        BeanFactory beanFactory = new XmlBeanFactory(springBeansResource);

        Registry r = buildFrameworkRegistry("SpringProvider.xml", false );

        SpringBeanFactoryHolder h = (SpringBeanFactoryHolder) r.getService(
                "gaderian.utilities.DefaultSpringBeanFactoryHolder",
                SpringBeanFactoryHolder.class);

        h.setBeanFactory(beanFactory);

        SimpleService a = (SimpleService) r.getService(
                "gaderian.test.utilities.Adder",
                SimpleService.class);

        assertEquals(17, a.add(9, 8));
    }
}