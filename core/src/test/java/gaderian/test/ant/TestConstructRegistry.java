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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.ops4j.gaderian.ant.ConstructRegistry;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.types.Path;

/**
 * Tests for the {@link org.ops4j.gaderian.ant.ConstructRegistry} Ant task.
 * <p>
 * An earlier version of this suite built using the real module.xml (in src/META-INF/org/ops4j/gaderian) but that
 * caused issues because it was constantly changing, so we use a copy
 * (src/test-data/TestConstructRegistry/master.xml).
 * <p>
 * These tests are *VERY* dependent on JDK version (really, on the version of the XML parser
 * provided with the JDK). Therefore, we skip the tests for JDK 1.3.
 * 
 * @author Howard Lewis Ship
 */
public class TestConstructRegistry extends FrameworkTestCase
{
    private static final boolean JDK1_3 = System.getProperty("java.version").startsWith("1.3.");

    protected Project _project = new Project();

    protected ConstructRegistry create()
    {
        Target t = new Target();

        ConstructRegistry result = new ConstructRegistry();
        result.setProject(_project);
        result.setOwningTarget(t);
        result.setTaskName("constructRegistry");

        return result;
    }

    public void testNoFile() throws Exception
    {
        ConstructRegistry cr = create();

        try
        {
            cr.execute();
            unreachable();
        }
        catch (BuildException ex)
        {
            assertExceptionSubstring(ex, "You must specify an output file");
        }
    }

    public void testNoDescriptors() throws Exception
    {
        ConstructRegistry cr = create();

        File f = File.createTempFile("testNoDescriptors-", ".xml");

        cr.setOutput(f);

        assertSame(f, cr.getOutput());

        try
        {
            cr.execute();
            unreachable();
        }
        catch (BuildException ex)
        {
            assertExceptionSubstring(ex, "You must specify a set of module descriptors");
        }

        f.delete();
    }

    public void testBasic() throws Exception
    {
        if (JDK1_3)
            return;

        ConstructRegistry cr = create();

        Path p = cr.createDescriptors();

        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/master.xml")));
        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/Symbols.xml")));

        File output = createTempRegistryFile("testBasic-");

        cr.setOutput(output);

        cr.execute();

        compare(output, getFrameworkPath("/test-data/TestConstructRegistry/testBasic.xml.master"));
    }

    public void testLocalRefs() throws Exception
    {
        if (JDK1_3)
            return;

        ConstructRegistry cr = create();

        Path p = cr.createDescriptors();

        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/LocalRefs.xml")));

        File output = createTempRegistryFile("testLocalRefs-");

        cr.setOutput(output);

        cr.execute();

        compare(
                output,
                getFrameworkPath("/test-data/TestConstructRegistry/testLocalRefs.xml.master"));
    }

    public void testUptoDate() throws Exception
    {
        if (JDK1_3)
            return;

        ConstructRegistry cr = create();

        Path p = cr.createDescriptors();

        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/master.xml")));
        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/Symbols.xml")));

        File output = createTempRegistryFile("testUptoDate-");

        cr.setOutput(output);

        cr.execute();

        compare(
                output,
                getFrameworkPath("/test-data/TestConstructRegistry/testUptoDate.xml.master"));

        long stamp = output.lastModified();

        cr.execute();

        assertEquals(stamp, output.lastModified());
    }

    public void testJars() throws Exception
    {
        if (JDK1_3)
            return;

        ConstructRegistry cr = create();

        Path p = cr.createDescriptors();

        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/master.xml")));
        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/empty.jar")));
        p.createPath().setLocation(
                new File(getFrameworkPath("/test-data/TestConstructRegistry/module.jar")));

        File output = createTempRegistryFile("testJars-");

        cr.setOutput(output);

        cr.execute();

        compare(output, getFrameworkPath("/test-data/TestConstructRegistry/testJars.xml.master"));
    }

    private File createTempRegistryFile(String prefix) throws IOException
    {
        File result = File.createTempFile(prefix, ".xml");

        // Delete the file, to force the task to re-create it.

        result.delete();

        result.deleteOnExit();

        return result;
    }

    protected void compare(File actual, String expectedPath) throws Exception
    {
        String expectedContent = readFile(new File(expectedPath));
        String actualContent = readFile(actual);

        assertEquals(expectedContent, actualContent);
    }

    protected String readFile(File f) throws Exception
    {
        InputStream in = new FileInputStream(f);
        BufferedInputStream bin = new BufferedInputStream(in);
        InputStreamReader reader = new InputStreamReader(bin);
        BufferedReader br = new BufferedReader(reader);
        LineNumberReader r = new LineNumberReader(br);

        StringBuffer buffer = new StringBuffer();

        while (true)
        {
            String line = r.readLine();

            if (line == null)
                break;

            buffer.append(line);
            buffer.append("\n");
        }

        r.close();

        return buffer.toString();
    }

}