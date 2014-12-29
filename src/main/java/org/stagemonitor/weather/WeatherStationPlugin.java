package org.stagemonitor.weather;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.StagemonitorPlugin;
import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.core.configuration.ConfigurationOption;
import org.stagemonitor.core.elasticsearch.ElasticsearchClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherStationPlugin implements StagemonitorPlugin {

	private static ScheduledExecutorService actionListenerMock = Executors.newScheduledThreadPool(1);

	private static double temp = 21.0;
	private static double humidity = 60;
	private static double pressure = 1000;
	private static double lumen = 1000;

	@Override
	public void initializePlugin(MetricRegistry metricRegistry, Configuration configuration) throws Exception {
		ElasticsearchClient.sendGrafanaDashboardAsync("Weather Station.json");

		metricRegistry.register("weather.temp", (Gauge<Double>) () -> temp);
		metricRegistry.register("weather.humidity", (Gauge<Double>) () -> humidity);
		metricRegistry.register("weather.pressure", (Gauge<Double>) () -> pressure);
		metricRegistry.register("weather.lumen", (Gauge<Double>) () -> lumen);
	}

	@Override
	public List<ConfigurationOption<?>> getConfigurationOptions() {
		return Collections.emptyList();
	}

	public static void main(String[] args)throws Exception {
		actionListenerMock.scheduleWithFixedDelay(() -> { temp += (Math.random() -0.5); }, 0, 1, TimeUnit.SECONDS);
		actionListenerMock.scheduleWithFixedDelay(() -> { humidity += Math.min(100, Math.random() - 0.5); }, 0, 1, TimeUnit.SECONDS);
		actionListenerMock.scheduleWithFixedDelay(() -> { pressure += (Math.random() -0.5); }, 0, 1, TimeUnit.SECONDS);
		actionListenerMock.scheduleWithFixedDelay(() -> { lumen += (Math.random() - 0.5); }, 0, 1, TimeUnit.SECONDS);

		final String instance = getInstance(args);
		System.out.println(instance);
		Stagemonitor.startMonitoring(getMeasurementSession(instance));

		while (!Thread.currentThread().isInterrupted()) {
			Thread.sleep(100);
		}
	}

	/**
	 * The instance could be the location the weather station is placed at. E.g. living room, bedroom, ...
	 *
	 * The instance name is extracted from the program arguments
	 *
	 * @param args the program arguments
	 * @return the instance ("Weather Station" per default)
	 */
	private static String getInstance(String[] args) {
		final String instance;
		if (args.length > 0) {
			instance = args[0];
		} else {
			instance = "Weather Station";
		} return instance;
	}

	static MeasurementSession getMeasurementSession(String instance) {
		final CorePlugin corePlugin = Stagemonitor.getConfiguration(CorePlugin.class);
		String applicationName = corePlugin.getApplicationName() != null ? corePlugin.getApplicationName() : "Weather Station";
		String instanceName = corePlugin.getInstanceName() != null ? corePlugin.getInstanceName() : instance;
		return new MeasurementSession(applicationName, MeasurementSession.getNameOfLocalHost(), instanceName);
	}

}
