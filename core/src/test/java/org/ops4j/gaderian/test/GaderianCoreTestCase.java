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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ops4j.gaderian.*;
import org.ops4j.gaderian.impl.*;
import org.ops4j.gaderian.internal.ser.ServiceSerializationHelper;
import org.ops4j.gaderian.testutils.GaderianTestCase;
import org.ops4j.gaderian.util.ClasspathResource;
import org.ops4j.gaderian.util.PropertyUtils;
import org.ops4j.gaderian.util.URLResource;

/**
 * Contains some support for creating Gaderian tests; this is useful enough that has been moved into
 * the main framework, to simplify creation of tests in the dependent libraries.
 *
 * @author Howard Lewis Ship
 */
public abstract class GaderianCoreTestCase extends GaderianTestCase
{
    // /CLOVER:OFF

    /**
     * An instance of {@link DefaultClassResolver} that can be used by tests.
     */
    private ClassResolver _classResolver;

    protected String _interceptedLoggerName;

    /**
     * Returns the given file as a {@link Resource} from the classpath. Typically, this is to find
     * files in the same folder as the invoking class.
     */
    protected Resource getResource(String file)
    {
        URL url = getClass().getResource(file);

        if (url == null)
            throw new NullPointerException("No resource named '" + file + "'.");

        return new URLResource(url);
    }

    /**
     * Removes the appender that may have been setup by {@link #interceptLogging(String)}. Also,
     * invokes {@link org.ops4j.gaderian.util.PropertyUtils#clearCache()}.
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        PropertyUtils.clearCache();

        ServiceSerializationHelper.setServiceSerializationSupport(null);
    }

    /**
     * Digs down through (potentially) a stack of ApplicationRuntimeExceptions until it reaches the
     * originating exception, which is returned.
     */
    protected Throwable findNestedException(ApplicationRuntimeException ex)
    {
        Throwable cause = ex.getRootCause();

        if (cause == null || cause == ex)
            return ex;

        if (cause instanceof ApplicationRuntimeException)
            return findNestedException((ApplicationRuntimeException) cause);

        return cause;
    }


    /**
     * Convienience method for invoking {@link #buildFrameworkRegistry(String[],boolean)} with only a single
     * file.
     */
    protected Registry buildFrameworkRegistry( String file, final boolean useStrictErrorHandler ) throws Exception
    {
        return buildFrameworkRegistry(new String[]
        { file }, useStrictErrorHandler );
    }

    /**
     * Builds a minimal registry, containing only the specified files, plus the master module
     * descriptor (i.e., those visible on the classpath). Files are resolved using
     * {@link GaderianCoreTestCase#getResource(String)}.
     * @param files The files to process
     * @param useStrictErrorHandler
     * @return The constructed registry
     * @throws Exception If an error occurs while building the registry
     */
    protected Registry buildFrameworkRegistry( String[] files, final boolean useStrictErrorHandler ) throws Exception
    {
        ClassResolver resolver = getClassResolver();

        List<Resource> descriptorResources = new ArrayList<Resource>();
        for ( String file : files )
        {
            Resource resource = getResource( file );

            descriptorResources.add( resource );
        }

        ModuleDescriptorProvider provider = new XmlModuleDescriptorProvider(resolver, descriptorResources);

        return buildFrameworkRegistry(provider, useStrictErrorHandler );
    }

    /**
     * Builds a registry, containing only the modules delivered by the specified
     * {@link org.ops4j.gaderian.ModuleDescriptorProvider}, plus the master module descriptor
     * (i.e., those visible on the classpath).
     */
    protected Registry buildFrameworkRegistry( ModuleDescriptorProvider customProvider, final boolean useStrictErrorHandler )
    {
        ClassResolver resolver = getClassResolver();

        RegistryBuilder builder = (useStrictErrorHandler ? new RegistryBuilder(new StrictErrorHandler()) : new RegistryBuilder());

        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(resolver));
        builder.addModuleDescriptorProvider(customProvider);

        return builder.constructRegistry(Locale.getDefault());
    }

    /**
     * Builds a registry from exactly the provided resource; this registry will not include the
     * <code>gaderian</code> module.
     */
    protected Registry buildMinimalRegistry(Resource l) throws Exception
    {
        RegistryBuilder builder = new RegistryBuilder();

        return builder.constructRegistry(Locale.getDefault());
    }

    private int _line = 1;

    /**
     * Returns a new {@link Location} instance. The resource is the test class, and the line number
     * increments by one from one for each invocation (thus each call will get a unique instance not
     * equal to any previously obtained instance).
     *
     * @since 1.1
     */
    protected Location newLocation()
    {
        String path = "/" + getClass().getName().replace('.', '/');

        Resource r = new ClasspathResource(getClassResolver(), path);

        return new LocationImpl(r, _line++);
    }

    /**
     * Returns a {@link DefaultClassResolver}. Repeated calls in the same test return the same
     * value.
     *
     * @since 1.1
     */

    protected ClassResolver getClassResolver()
    {
        if (_classResolver == null)
            _classResolver = new DefaultClassResolver();

        return _classResolver;
    }

}
