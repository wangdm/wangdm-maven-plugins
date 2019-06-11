package cn.wangdm.maven.plugin.fake;

import java.io.File;
import java.io.IOException;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "fake", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FakeMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

    /**
     * The filename to be used for the generated archive file. For the source:jar goal, "-sources" is appended to this
     * filename. For the source:test-jar goal, "-test-sources" is appended.
     */
    @Parameter( defaultValue = "${project.build.finalName}" )
    protected String finalName;

    /**
     * The Maven Project Object
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * The Maven session.
     */
    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;

    /**
     * The Jar archiver.
     */
    @Component( role = Archiver.class, hint = "jar" )
    private JarArchiver jarArchiver;

    /**
     * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
     * Archiver Reference</a>. <br/>
     * <b>Note: Since 3.0.0 the resulting archives contain a maven descriptor. If you need to suppress the generation of
     * the maven descriptor you can simply achieve this by using the
     * <a href="http://maven.apache.org/shared/maven-archiver/index.html#archive">archiver configuration</a>.</b>.
     * 
     * @since 2.1
     */
    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	public void execute() throws MojoExecutionException {
		getLog().info( "Hello, world." );

        if ( !"pom".equals( project.getPackaging() ) )
        {
    		File f = outputDirectory;

    		if (!f.exists()) {
    			f.mkdirs();
    		}
    		
            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );

    		File readme = new File(f, "README");
    		if(!readme.exists()) {
    			try {
					readme.createNewFile();
				} catch (IOException e) {
				}
    		}
    		archiver.getArchiver().addFile(readme, "README");
            File outputFile = new File( outputDirectory, finalName + "-sources.jar");
            archiver.setOutputFile(outputFile);
            try {
				archiver.createArchive(session, project, archive);
			} catch (ManifestException | IOException | DependencyResolutionRequiredException e) {
			}
            readme.delete();
        }
	}
}
