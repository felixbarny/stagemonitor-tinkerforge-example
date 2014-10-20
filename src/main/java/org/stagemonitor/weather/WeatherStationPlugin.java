package org.stagemonitor.weather;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.StagemonitorPlugin;
import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.core.configuration.ConfigurationOption;
import org.stagemonitor.core.configuration.ConfigurationSource;
import org.stagemonitor.core.configuration.SimpleSource;
import org.stagemonitor.core.rest.RestClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherStationPlugin implements StagemonitorPlugin {

	private static ScheduledExecutorService actionListenerMock = Executors.newScheduledThreadPool(1);

	private static double temp;

	@Override
	public void initializePlugin(MetricRegistry metricRegistry, Configuration configuration) throws Exception {
		RestClient.sendGrafanaDashboardAsync(configuration.getConfig(CorePlugin.class).getElasticsearchUrl(), "Weather Station.json");

		metricRegistry.register("weather.temp", (Gauge<Double>) () -> temp);
	}

	@Override
	public List<ConfigurationOption<?>> getConfigurationOptions() {
		return Collections.emptyList();

	}

	public static void main(String[] args)throws Exception {
		actionListenerMock.schedule(() -> { temp = Math.random() * 100; }, 1, TimeUnit.SECONDS);
		Stagemonitor.startMonitoring(getMeasurementSession(), getConfiguration(args));

		while (!Thread.currentThread().isInterrupted()) {
			Thread.sleep(100);
		}
	}

	static MeasurementSession getMeasurementSession() {
		final CorePlugin corePlugin = Stagemonitor.getConfiguration(CorePlugin.class);
		String applicationName = corePlugin.getApplicationName() != null ? corePlugin.getApplicationName() : "Weather Station";
		String instanceName = corePlugin.getInstanceName() != null ? corePlugin.getInstanceName() : "host";
		return new MeasurementSession(applicationName, getHostName(), instanceName);
	}

	static ConfigurationSource getConfiguration(String[] args) {
		final SimpleSource source = new SimpleSource("Process Arguments");
		for (String arg : args) {
			if (!arg.matches("(.+)=(.+)")) {
				throw new IllegalArgumentException("Illegal argument '" + arg +
						"'. Arguments must be in form '<config-key>=<config-value>'");
			}
			final String[] split = arg.split("=");
			source.add(split[0], split[1]);
		}
		return source;
	}

	static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return getHostNameFromEnv();
		}
	}

	static String getHostNameFromEnv() {
		// try environment properties.
		String host = System.getenv("COMPUTERNAME");
		if (host != null) {
			return host;
		}
		host = System.getenv("HOSTNAME");
		if (host != null) {
			return host;
		}
		return null;
	}
}
