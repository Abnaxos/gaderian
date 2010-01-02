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

package org.ops4j.gaderian.utilities.groovy;

import java.net.URL;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.reportMatcher;
import org.easymock.IArgumentMatcher;
import org.easymock.internal.EqualsMatcher;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.impl.DefaultErrorHandler;
import org.ops4j.gaderian.parse.DescriptorParser;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.util.URLResource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

public class TestGaderianBuilder extends GaderianCoreTestCase
{
    public void testBasicScript() throws Exception
    {
        ContentHandler mock = createMock(ContentHandler.class);

        mock.setDocumentLocator(GaderianBuilder.GROOVY_LOCATOR);

        AttributesImpl attrs = new AttributesImpl();

        attrs.addAttribute("", "id", "id", "", "basic");
        attrs.addAttribute("", "version", "version", "", "1.0.0");

        mock.startElement( eq(""), eq("module"), eq("module"), attributesMatches(attrs));
        mock.endElement("", "module", "module");

        replayAllRegisteredMocks();

        Script script = new GroovyShell().parse("processor.module(id:'basic', version:'1.0.0')");

        runScript(script, mock);

        verifyAllRegisteredMocks();

    }

    public void testLinePreciseErrorReporting() throws Exception
    {
        ErrorHandler handler = new DefaultErrorHandler();
        DescriptorParser parser = new DescriptorParser(handler);

        ClassResolver classResolver = createMock( ClassResolver.class );
        Resource resource = createMock( Resource.class );

        replayAllRegisteredMocks();

        final URL resourceURL = getResourceURL( "missingModuleId.groovy" );
        parser.initialize(new URLResource( resourceURL ), classResolver);

        GroovyCodeSource source = new GroovyCodeSource( resourceURL );

        Script script = new GroovyShell().parse(source);

        try
        {
            runScript(script, parser);

            unreachable();
        }
        catch (ApplicationRuntimeException e)
        {
            assertExceptionRegexp(e,"Missing required attribute .+missingModuleId\\.groovy, line 15\\)\\.");
        }
        verifyAllRegisteredMocks();
    }

    private void runScript(Script script, ContentHandler handler)
    {
        GaderianBuilder builder = new GaderianBuilder(handler);

        Binding processorBinding = new Binding();
        processorBinding.setVariable("processor", builder);

        script.setBinding(processorBinding);

        script.run();
    }

    private Attributes attributesMatches(final Attributes expectedAttributes)
    {
        reportMatcher( new IArgumentMatcher()
        {
            public boolean matches( final Object argument )
            {
                Attributes actualAttributes = (Attributes) argument;

                if (expectedAttributes.getLength() != actualAttributes.getLength())
                    return false;

                for (int i = 0; i < expectedAttributes.getLength(); i++)
                {
                    if (!expectedAttributes.getLocalName(i)
                            .equals(actualAttributes.getLocalName(i))
                            || !expectedAttributes.getValue(i).equals(actualAttributes.getValue(i)))
                        return false;
                }
                return true;
            }

            public void appendTo( final StringBuffer buffer )
            {
                buffer.append("attributesMatches(" + expectedAttributes + ")");
            }
        } );
        return null;
    }

}