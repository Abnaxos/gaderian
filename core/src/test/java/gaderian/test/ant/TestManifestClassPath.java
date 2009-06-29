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

package gaderian.test.ant;

import gaderian.test.FrameworkTestCase;

import java.io.File;

import org.ops4j.gaderian.ant.ManifestClassPath;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.types.Path;

/**
 * Tests the {@link org.ops4j.gaderian.ant.ManifestClassPath}
 * Ant task.
 *
 * @author Howard Lewis Ship
 */
public class TestManifestClassPath extends FrameworkTestCase
{
    protected Project _project = new Project();

    protected ManifestClassPath create()
    {
        Target t = new Target();

        ManifestClassPath result = new ManifestClassPath();
        result.setProject(_project);
        result.setOwningTarget(t);
        result.setTaskName("manifestClassPath");

        return result;
    }

    public void testSimple() throws Exception
    {
        ManifestClassPath mcp = create();

        mcp.setProperty("output");

        assertEquals("output", mcp.getProperty());

        Path path = mcp.createClasspath();

        Path.PathElement pe = path.createPathElement();

        pe.setLocation(new File("src/META-INF/org/ops4j/gaderian/module.xml"));

        pe = path.createPathElement();

        pe.setLocation(new File("src/java/org/apache/commons/gaderian/HiveMind.properties"));

        mcp.execute();

        assertEquals("module.xml HiveMind.properties", _project.getProperty("output"));
    }

    public void testDirectory() throws Exception
    {
        ManifestClassPath mcp = create();

        mcp.setProperty("output");

        assertEquals("output", mcp.getProperty());

        File dir = new File("src").getAbsoluteFile();

        mcp.setDirectory(dir);

        assertEquals(dir, mcp.getDirectory());

        Path path = mcp.createClasspath();

        Path.PathElement pe = path.createPathElement();

        pe.setLocation(new File("src/META-INF/org/ops4j/gaderian/module.xml"));

        pe = path.createPathElement();

        pe.setLocation(new File("src/java/org/apache/commons/gaderian/HiveMind.properties"));

        pe = path.createPathElement();

        pe.setLocation(new File("common/links.xml"));

        mcp.execute();

        assertEquals(
            "META-INF/org/ops4j/gaderian/module.xml java/org/apache/commons/gaderian/HiveMind.properties",
            _project.getProperty("output"));
    }

    public void testDirectoryInClasspath() throws Exception
    {
        ManifestClassPath mcp = create();

        mcp.setProperty("output");

        File dir = new File("src").getAbsoluteFile();

        mcp.setDirectory(dir);

        Path path = mcp.createClasspath();

        Path.PathElement pe = path.createPathElement();

        pe.setLocation(dir);

        pe = path.createPathElement();

        pe.setLocation(new File("src/META-INF/org/ops4j/gaderian/module.xml"));

        mcp.execute();

        assertEquals("META-INF/org/ops4j/gaderian/module.xml", _project.getProperty("output"));
    }

    public void testEmpty() throws Exception
    {
        ManifestClassPath mcp = create();

        mcp.setProperty("zap");

        assertEquals("zap", mcp.getProperty());

        mcp.createClasspath();

        mcp.execute();

        assertEquals("", _project.getProperty("zap"));

    }

    public void testNoProperty() throws Exception
    {
        ManifestClassPath mcp = create();

        mcp.createClasspath();

        try
        {
            mcp.execute();

            unreachable();
        }
        catch (BuildException ex)
        {
            assertExceptionSubstring(
                ex,
                "You must specify a property to assign the manifest classpath to");
        }

    }

    public void testNoClasspath() throws Exception
    {
        ManifestClassPath mcp = create();

        mcp.setProperty("bar");

        try
        {
            mcp.execute();

            unreachable();
        }
        catch (BuildException ex)
        {
            assertExceptionSubstring(
                ex,
                "You must specify a classpath to generate the manifest entry from");
        }

    }
}
