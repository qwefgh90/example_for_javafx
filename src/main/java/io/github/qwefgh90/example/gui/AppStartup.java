package io.github.qwefgh90.example.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.WebApplicationContext;

import javafx.application.Platform;

/**
 * local file contents search engine with javafx webview and spring restful api
 * 
 * @author choechangwon
 */
public class AppStartup{

	// initial variable
	public static boolean TEST_MODE = false;

	// exchagable to TEST_APP_DATA_DIR_NAME
	public final static String APP_DATA_DIR_NAME = "appdata";
	public final static String WEB_APP_DIRECTORY_NAME = "app";
	public final static Path deployedPath;
	public final static Path parentOfClassPath;
	public final static Path pathForLog4j;
	public final static Path pathForAppdata;
	public final static Path tomatLoggingFilePath;
	public final static Path appLoggingFilePath;
	public final static String address;
	public final static int port;
	public final static String homeUrl;

	public final static String PAGE = "/" + WEB_APP_DIRECTORY_NAME
			+ "/index.html";
	
	private final static Logger LOG = LoggerFactory
			.getLogger(AppStartup.class);

	/**
	 * application system variable is initialized. deploy resources.
	 */
	static {
		final boolean isProduct = isJarStart();
		// Application Path
		deployedPath = getCurrentBuildPath(); //Jar file or Classpath
		parentOfClassPath = deployedPath.getParent();
		
		if (isProduct) {
			pathForLog4j = parentOfClassPath.resolve("log4j.xml");
			pathForAppdata = parentOfClassPath.resolve(APP_DATA_DIR_NAME);
		}
		else{
			pathForLog4j = deployedPath.resolve("log4j.xml");			//possible to redeploy on dev mode
			pathForAppdata = deployedPath.resolve(APP_DATA_DIR_NAME);	//possible to redeploy on dev mode
		}
		
		tomatLoggingFilePath = pathForAppdata.resolve("catalina.out");
		appLoggingFilePath = pathForAppdata.resolve("sample.log");

		address = "127.0.0.1";
		port = findFreePort();
		homeUrl = "http://" + address + ":" + port;

		if (!Files.isWritable(parentOfClassPath)) {
			// Can not write
			throw new RuntimeException("can't write resource classpath");
		} else if (Files.exists(pathForAppdata)) {
			// Pass
		} else {
			// Create appdata directory
			try {
				Files.createDirectory(pathForAppdata);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Deploy resource
		try {
			if (isProduct) { // Run in Jar
				AppStartup.copyFileInJar(deployedPath.toString(), pathForLog4j.getFileName().toString(),
						parentOfClassPath.toFile(), (file) -> {return !file.exists();});
				
				System.out.println("Initializing log4j with: " + pathForLog4j);
				DOMConfigurator.configureAndWatch(pathForLog4j.toAbsolutePath().toString());
				
				// Resources in jar are copied to deployed directory
				copyDirectoryInJar(deployedPath.toString(), APP_DATA_DIR_NAME,
						parentOfClassPath.toFile(), (File file, JarEntry entry) -> file.lastModified() < entry.getLastModifiedTime().toMillis());
			} 
		} catch (URISyntaxException e) {
			LOG.error("fail to copy resource to app directory");
			throw new RuntimeException(ExceptionUtils.getStackTrace(e));
		} catch (IOException e) {
			LOG.error("fail to copy resource to app directory");
			throw new RuntimeException(ExceptionUtils.getStackTrace(e));
		}

		final StringBuilder logBuilder = new StringBuilder();
		logBuilder.append("\n")
				.append("[Information]").append("\n")
				.append("* classpath: ").append(getCurrentBuildPath()).append("\n")
				.append("* appdata path: ").append(pathForAppdata.toString()).append("\n")
				.append("* log4j path: ").append(pathForLog4j.toString());
		LOG.info(logBuilder.toString());

		logBuilder.setLength(0);
	}

	public static void main(String[] args) {
		new GUIApplication().start(args); // sync function // can't bean in spring container.
	}

	/**
	 * Returns a free port number on localhost, or throw runtime exception if
	 * unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free
	 *         port
	 * @since 3.0
	 */
	public static int findFreePort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (IOException e) {
		}
		throw new RuntimeException("no available ports.");
	}

	/**
	 * get execution jar path
	 * 
	 * @return String - path
	 */
	public static Path getCurrentBuildPath() {
		if (getResourcePath("") == null) {
			URI uri;
			try {
				uri = AppStartup.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI();
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return null;
			}
			return Paths.get(uri);
		} else {
			return Paths.get(getResourcePath(""));
		}
	}

	public static boolean isJarStart() {
		return getCurrentBuildPath().toString().endsWith(".jar");
	}

	/**
	 * Handle resourceName, whether File.separator exists or not
	 * 
	 * @param dirName
	 * @return
	 */
	public static String getResourcePath(String dirName) {
		try {
			return new ClassPathResource(dirName).getFile().getAbsolutePath();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * jar files and Copy to destination. If a file exists, overwrite it.<br>
	 * relative reference - http://cs.dvc.edu/HowTo_ReadJars.html jar resource
	 * use forward slash (/)
	 * 
	 * @param jarPath
	 * @param resourceDirInJar
	 *            - "/config" or "/config/" or "config" or ""
	 * @param destinationRoot
	 * @throws URISyntaxException
	 * @throws IOException
	 * @author qwefgh90
	 */
	public static void copyDirectoryInJar(String jarPath,
			String resourceDirInJar, File destinationRoot, BiFunction<File, JarEntry, Boolean> destFileFilter)
			throws URISyntaxException, IOException {
		if(destFileFilter == null)
			destFileFilter = (file,entry) -> true;
		if (resourceDirInJar.startsWith("/")) { // jar url start with /
												// replace to jar
												// entry style which
												// is not start with
												// '/'
			resourceDirInJar = resourceDirInJar.substring(1);
		}
		if (resourceDirInJar.length() != 0
				&& resourceDirInJar.getBytes()[resourceDirInJar.length() - 1] != File.separator
						.getBytes()[0]) // add
										// rightmost
										// seperator
			resourceDirInJar = resourceDirInJar + "/";

		LOG.trace("package extract info : " + "\nFile.separator : "
				+ File.separator + "\nresourceDirInJar : " + resourceDirInJar
				+ "\njarPath : " + jarPath + "\ndestinationRoot"
				+ destinationRoot);

		FileInputStream fis = new FileInputStream(jarPath);
		JarInputStream jis = new JarInputStream(fis);
		JarEntry entry = jis.getNextJarEntry();
		// loop entry
		while (entry != null) {
			LOG.trace("extract from java : " + entry.getName());
			if (entry.getName().startsWith(resourceDirInJar) // Directory in jar
					&& entry.getName().endsWith("/")) {
				LOG.trace("try to copy : " + entry.getName());
				Files.createDirectories(new File(destinationRoot, entry
						.getName()).toPath());
			} else if (entry.getName().startsWith(resourceDirInJar) // File in jar
					&& !entry.getName().endsWith("/")) {
				File destFile = new File(destinationRoot.getAbsolutePath(), entry
						.getName());
				
				if(!destFileFilter.apply(destFile, entry)){
					LOG.debug("skip copy : " + entry.getName());
					
				}else{
					LOG.debug("copy start : " + entry.getName());
					File tempFile = extractTempFile(getResourceInputstream(entry
							.getName()));
					FileUtils.copyFile(
							tempFile,
							new File(destinationRoot.getAbsolutePath(), entry
									.getName())); // copy
					tempFile.delete();
				}
			}
			entry = jis.getNextJarEntry();
		}
		jis.close();
	}
	
	public static void copyFileInJar(String jarPath, String resourcePathInJar,
			File destinationRootDir, FileFilter destFileFilter)
			throws URISyntaxException, IOException {
		if(destFileFilter == null)
			destFileFilter = (file) -> true;
		if (resourcePathInJar.startsWith("/")) { // jar url start with /
													// replace to jar
													// entry getName() style which
													// is not start with
													// '/'
			resourcePathInJar = resourcePathInJar.substring(1);
		}

		FileInputStream fis = new FileInputStream(jarPath);
		JarInputStream jis = new JarInputStream(fis);
		JarEntry entry = jis.getNextJarEntry();
		// loop entry
		while (entry != null) {
			if (entry.getName().startsWith(resourcePathInJar) // File in jar
					&& entry.getName().getBytes()[entry.getName().length() - 1] != File.separator
							.getBytes()[0]) {
				int lastIndex = entry.getName().lastIndexOf("/");
				String entryName;
				if (lastIndex != -1)
					entryName = entry.getName().substring(lastIndex);
				else
					entryName = entry.getName();
				File destFile = new File(destinationRootDir.getAbsolutePath(),entryName);
				
				if(!destFileFilter.accept(destFile)){
					LOG.debug("skip copy : " + entry.getName());
				}else{
					LOG.debug("copy start : " + entry.getName());
					File tempFile = extractTempFile(getResourceInputstream(entry
							.getName()));
					FileUtils.copyFile(
								tempFile,destFile); // copy from source file to destination file
					tempFile.delete();
				}
			}
			entry = jis.getNextJarEntry();
		}
		jis.close();
	}

	/**
	 * Resource input stream in jar
	 * 
	 * @param resourceName
	 * @return
	 */
	public static InputStream getResourceInputstream(String resourceName) {
		return AppStartup.class.getClassLoader().getResourceAsStream(
				resourceName);
	}

	/**
	 * This method is responsible for extracting resource files from within the
	 * .jar to the temporary directory.
	 * 
	 * @param input
	 *            - returned value of
	 *            getClassLoader().getResourceAsStream("config/help.txt");
	 * @return Temp file created by stream
	 * @throws IOException
	 */
	private static File extractTempFile(InputStream input) throws IOException {
		File f = File.createTempFile("Thistempfile", "willdelete");
		FileOutputStream tempFileos = new FileOutputStream(f);
		byte[] byteArray = new byte[1024];
		int i;
		// While the input stream has bytes
		while ((i = input.read(byteArray)) > 0) {
			// Write the bytes to the output stream
			tempFileos.write(byteArray, 0, i);
		}
		// Close streams to prevent errors
		input.close();
		tempFileos.close();
		return f;

	}

	/*
	 * dirty hack use it if can't annotated base injection
	 */
	private static WebApplicationContext rootAppContext;
	private static WebApplicationContext servletAppContext;

	public static <T> T getBean(Class<T> c) {
		T svc = servletAppContext.getBean(c);
		return svc;
	}

	public static void setRootAppContext(WebApplicationContext rootAppContext) {
		AppStartup.rootAppContext = rootAppContext;
	}

	public static void setServletAppContext(WebApplicationContext servletAppContext) {
		AppStartup.servletAppContext = servletAppContext;
	}


}
