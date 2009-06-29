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

package gaderian.test;

import gaderian.test.config.impl.Datum;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;

import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.impl.DefaultClassResolver;
import org.ops4j.gaderian.impl.RegistryBuilder;
import org.ops4j.gaderian.impl.XmlModuleDescriptorProvider;
import org.ops4j.gaderian.parse.ModuleDescriptor;
import org.ops4j.gaderian.service.ClassFactory;

/**
 * Tests the {@link org.ops4j.gaderian.impl.RegistryBuilder} class.
 * 
 * @author Howard Lewis Ship
 */
public class TestRegistryBuilder extends FrameworkTestCase
{
    /**
     * Reproduce test {@link gaderian.test.config.TestConfiguration#testValueVariables()} using
     * dynamic lookup of module resources.
     */

    public void testLookup() throws Exception
    {
        // JDK 1.3 URLClassLoader doesn't seem to work properly for directories, so
        // the contents of the TestRegistryBuilder folder are packaged inside this JAR.

        File f = new File(getFrameworkPath("/test-data/TestRegistryBuilder.jar"));

        URL[] urls = new URL[]
        { f.toURL() };

        ClassLoader loader = new URLClassLoader(urls, Thread.currentThread()
                .getContextClassLoader());

        ClassResolver resolver = new DefaultClassResolver(loader);

        RegistryBuilder b = new RegistryBuilder();

        b.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(resolver));

        Registry r = b.constructRegistry(Locale.getDefault());

        List l = r.getConfiguration("gaderian.test.config.Symbols");
        assertEquals(1, l.size());

        Datum d = (Datum) l.get(0);

        assertEquals("wife", d.getKey());
        assertEquals("wilma", d.getValue());
    }

    public void testConstructDefaultRegistry() throws Exception
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ClassFactory factory = (ClassFactory) r.getService(
                "gaderian.ClassFactory",
                ClassFactory.class);

        assertNotNull(factory);
    }

    public void testDuplicateModuleId() throws Exception
    {
        String duplicateModuleId = "non.unique.module";

        ModuleDescriptor firstModule = createModuleDescriptor(duplicateModuleId, null);
        ModuleDescriptor duplicateModule = createModuleDescriptor(duplicateModuleId, null);

        SimpleModuleDescriptorProvider provider = new SimpleModuleDescriptorProvider();
        provider.addModuleDescriptor(firstModule);
        provider.addModuleDescriptor(duplicateModule);

        interceptLogging();

        buildFrameworkRegistry(provider);

        assertLoggedMessagePattern("Module " + duplicateModuleId + " is duplicated!");
    }

    public void testDuplicateExtensionPoints() throws Exception
    {
        ModuleDescriptor testModule = createModuleDescriptor("gaderian.test", null);

        testModule.addServicePoint(createServicePointDescriptor("MyService", Comparable.class));
        testModule.addServicePoint(createServicePointDescriptor("MyService", Comparable.class));

        testModule.addConfigurationPoint(createConfigurationPointDescriptor("MyConfig"));
        testModule.addConfigurationPoint(createConfigurationPointDescriptor("MyConfig"));

        SimpleModuleDescriptorProvider provider = new SimpleModuleDescriptorProvider();
        provider.addModuleDescriptor(testModule);

        interceptLogging();

        buildFrameworkRegistry(provider);

        List interceptedEvents = getInterceptedLogEvents();

        assertLoggedMessagePattern("Extension point gaderian\\.test\\.MyService "
                + "conflicts with definition at .* and has been ignored\\.", interceptedEvents);
        assertLoggedMessagePattern("Extension point gaderian\\.test\\.MyConfig "
                + "conflicts with definition at .* and has been ignored\\.", interceptedEvents);
    }
}