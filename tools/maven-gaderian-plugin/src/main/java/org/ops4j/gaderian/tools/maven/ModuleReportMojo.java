package org.ops4j.gaderian.tools.maven;

import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Resource;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.ops4j.gaderian.ant.RegistrySerializer;
import org.ops4j.gaderian.impl.XmlModuleDescriptorProvider;
import org.ops4j.gaderian.impl.DefaultClassResolver;
import org.ops4j.gaderian.util.FileResource;

import org.codehaus.plexus.util.DirectoryScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @goal module-report
 * @requiresDependencyResolution runtime
 * @phase site
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ModuleReportMojo extends AbstractMavenReport {

    /**
     * @component
     */
    private Renderer siteRenderer;

    /**
     * @parameter expression="module-report"
     * @required
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private String outputName;

    /**
     * @parameter expression="${project.reporting.outputDirectory}"
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private File outputDir;

    /**
     * @parameter expression="../apidocs"
     * @required
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private String javadocPath;

    /**
     * @parameter expression="true"
     */
    private boolean linkJavadoc;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private MavenProject project;

    /**
     * @parameter expression="${reactorProjects}"
     * @required
     * @readonly
     */
    @SuppressWarnings({ "UnusedDeclaration", "MismatchedQueryAndUpdateOfCollection" })
    private List<MavenProject> reactorProjects;

    /**
     * @parameter
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private String[] includes = { "**/module.xml" };

    /**
     * @parameter
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    private String[] excludes = null;

    private List<File> hivemodules = null;

    public boolean isExternalReport() {
        return true;
    }

    @Override
    public boolean canGenerateReport() {
        scan();
        return !hivemodules.isEmpty();
    }

    protected void executeReport(Locale locale) throws MavenReportException {
        //if ( !project.isExecutionRoot() ) {
        //    getLog().warn("Not execution root, I should not have been called");
        //    return;
        //}
        scan();
        if ( hivemodules.isEmpty() ) {
            getLog().warn("No modules found");
            return;
        }
        RegistrySerializer serializer = new RegistrySerializer();
        for ( File module : hivemodules ) {
            getLog().info("Adding module: " + module);
            serializer.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(new DefaultClassResolver(), new FileResource(module.toString())));
        }
        File outputDir = new File(this.outputDir, outputName);
        if ( !outputDir.isDirectory() && !outputDir.mkdirs() ) {
            throw new MavenReportException("Could not create directory " + outputDir);
        }
        File outputFile = new File(outputDir, "index.html");
        InputStream xslt = null;
        OutputStream registry = null;
        try {
            xslt = new BufferedInputStream(getClass().getResourceAsStream("module-report.xsl"));
            registry = new BufferedOutputStream(new FileOutputStream(outputFile));
            Document document = serializer.createRegistryDocument();
            fixDOMTree(document);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));
            transformer.setParameter("base.dir", outputDir);
            // FIXME: make this configurable
            if ( linkJavadoc && project.isExecutionRoot() ) {
                transformer.setParameter("javadoc.path", javadocPath);
            }
            else {
                transformer.setParameter("javadoc.path", "");
            }
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(registry);
            transformer.transform(source, result);
        }
        catch ( TransformerConfigurationException e ) {
            throw new MavenReportException("Could not write " + outputFile, e);
        }
        catch ( TransformerException e ) {
            throw new MavenReportException("Could not write " + outputFile, e);
        }
        catch ( FileNotFoundException e ) {
            throw new MavenReportException("Could not write " + outputFile, e);
        }
        finally {
            if ( xslt != null ) {
                try {
                    xslt.close();
                }
                catch ( IOException e ) {
                    getLog().warn("Error closing XSLT file", e);
                }

            }
            if ( registry != null ) {
                try {
                    registry.close();
                }
                catch ( IOException e ) {
                    getLog().warn("Error closing output file " + outputFile, e);
                }

            }
        }
        try {
            copyResource("module-report.css", outputDir);
            copyResource("private.png", outputDir);
            copyResource("public.png", outputDir);
        }
        catch ( IOException e ) {
            throw new MavenReportException("Error copying resources", e);
        }
    }

    private void scan() {
        if ( hivemodules == null ) {
            hivemodules = new LinkedList<File>();
            if ( project.isExecutionRoot() ) {
                for ( MavenProject prj : reactorProjects ) {
                    scanProject(prj);
                }
            }
            else {
                scanProject(project);
            }
        }
    }

    private void scanProject(MavenProject prj) {
        List<Resource> resources = prj.getResources();
        for ( Resource rsrc : resources ) {
            File dir = new File(rsrc.getDirectory());
            if ( dir.isDirectory() ) {
                DirectoryScanner scanner = new DirectoryScanner();
                scanner.setIncludes(includes);
                scanner.setExcludes(excludes);
                scanner.setBasedir(dir);
                scanner.scan();
                for ( String f : scanner.getIncludedFiles() ) {
                    hivemodules.add(new File(dir, f));
                }
            }
        }
    }

    public String getOutputName() {
        return outputName + "/index";
    }

    public String getName(Locale locale) {
        return "Gaderian Modules";
    }

    public String getDescription(Locale locale) {
        return "Gaderian registry documentation";
    }


    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    protected String getOutputDirectory() {
        return outputName;
        //return outputDir.toString();
        //return new File(outputDir, outputName);
    }

    protected MavenProject getProject() {
        return project;
    }

    //public void execute() throws MavenReportException, MojoFailureException {
    //}

    private void fixDOMTree(Node node) {
        if ( node.getNodeType() == Node.TEXT_NODE && node.getTextContent() == null ) {
            node.setTextContent("");
        }
        if ( node.hasChildNodes() ) {
            NodeList children = node.getChildNodes();
            for ( int i = 0; i < children.getLength(); i++ ) {
                fixDOMTree(children.item(i));
            }
        }
    }

    private void copyResource(String resource, File targetDir) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new BufferedInputStream(getClass().getResourceAsStream(resource));
            output = new BufferedOutputStream(new FileOutputStream(new File(targetDir, resource)));
            int b;
            while ( (b = input.read()) >= 0 ) {
                output.write(b);
            }
        }
        finally {
            if ( input != null ) {
                try {
                    input.close();
                }
                catch ( IOException e ) {
                    getLog().warn("Error closing input stream for resource " + resource);
                }
            }
            if ( output!= null ) {
                try {
                    output.close();
                }
                catch ( IOException e ) {
                    getLog().warn("Error closing output file " + targetDir);
                }
            }
        }
    }

}
