package io.github.qwefgh90.example.gui;

import static io.github.qwefgh90.example.gui.Java2JavascriptUtils.connectBackendObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GUIApplication extends Application {
	private final static Logger LOG = LoggerFactory
			.getLogger(GUIApplication.class);

	private final double WINDOW_LOADING_WIDTH = 300;
	private final double WINDOW_LOADING_HEIGHT = 330;
	private final Tomcat tomcat = new Tomcat();
	
	private WebView currentView;
	private Stage primaryStage;
	
	public void start(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws ServletException, LifecycleException {
		this.primaryStage = primaryStage;
		LOG.info("JavaFX Application is Loading");
		tomcat.getConnector().setAttribute("address",
				AppStartup.address);
		tomcat.getConnector().setAttribute("port",
				AppStartup.port);

		final Context context = tomcat.addWebapp("",
				AppStartup.pathForAppdata.toAbsolutePath()
				.toString());
		// https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/catalina/startup/Tomcat.html#addWebapp(org.apache.catalina.Host,%20java.lang.String,%20java.lang.String)

		context.setJarScanner(new FastJarScanner());
		context.addWelcomeFile(AppStartup.PAGE);
		tomcat.init();
		tomcat.start();
		primaryStage.show();
		currentView = initializeWebviewWhenComplete();
	}
	

	@Override
	public void init() throws Exception {
		super.init();
	}
	
	@Override
	public void stop() throws Exception {
		LOG.info("try stop tomcat");
		tomcat.stop();
		LOG.info("stopped tomcat");
		super.stop();
		LOG.info("javafx stop()");
	}

	private WebView initializeWebviewWhenComplete() {
		final WebView webView = new WebView();
		// show "alert" Javascript messages in stdout (useful to debug)
		webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> arg0) {
				System.err.println("alert: " + arg0.getData());
			}
		});

		// load index.html
		// webView.getEngine().load(getClass().getResource(page).toExternalForm());
		webView.getEngine().load(AppStartup.homeUrl + AppStartup.PAGE);

		primaryStage.setScene(new Scene(webView));
		primaryStage.setTitle("Example");

		final Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		final double WINDOW_DEFAULT_X;
		final double WINDOW_DEFAULT_Y;
		final double WINDOW_DEFAULT_WIDTH;
		final double WINDOW_DEFAULT_HEIGHT;
		
		WINDOW_DEFAULT_X =  primaryScreenBounds.getWidth() / 10;
		WINDOW_DEFAULT_WIDTH = primaryScreenBounds.getWidth()/2 < 520 ? primaryScreenBounds.getWidth() : 520;
		WINDOW_DEFAULT_Y = primaryScreenBounds.getHeight() / 4;
		WINDOW_DEFAULT_HEIGHT = WINDOW_DEFAULT_WIDTH;

		final Preferences userPrefs = Preferences
				.userNodeForPackage(AppStartup.class);

		double w = userPrefs.getDouble("stage.width", WINDOW_DEFAULT_WIDTH);
		double h = userPrefs.getDouble("stage.height", WINDOW_DEFAULT_HEIGHT);
		if(w <= WINDOW_LOADING_WIDTH)
			w = WINDOW_DEFAULT_WIDTH;
		if(h <= WINDOW_LOADING_HEIGHT)
			h = WINDOW_DEFAULT_HEIGHT;
		
		primaryStage.setX(userPrefs.getDouble("stage.x", WINDOW_DEFAULT_X));
		primaryStage.setY(userPrefs.getDouble("stage.y", WINDOW_DEFAULT_Y));
		primaryStage.setWidth(w);
		primaryStage.setHeight(h);

		LOG.info("Sample is ready : " + AppStartup.homeUrl);
		return webView;
	}
	
	/**
	 * Fast Jar Scanner scans one kind of jar like handyfinder.jar
	 * 
	 * @author cheochangwon
	 *
	 */
	private static class FastJarScanner extends StandardJarScanner {
		@Override
		public void scan(JarScanType scanType, ServletContext context,
				JarScannerCallback callback) {
			final StandardJarScanFilter filter = new StandardJarScanFilter();
			filter.setDefaultTldScan(false);
			filter.setPluggabilitySkip("*.jar");
			filter.setPluggabilityScan("*handyfinder*");
			setJarScanFilter(filter);

			super.scan(scanType, context, callback);
		}

		@Override
		public void setJarScanFilter(JarScanFilter jarScanFilter) {
		 	super.setJarScanFilter(jarScanFilter);
		}
	}
}
