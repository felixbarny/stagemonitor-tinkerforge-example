package org.stagemonitor.weather;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.StagemonitorPlugin;
import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.core.elasticsearch.ElasticsearchClient;
import org.stagemonitor.weather.mock.MockBrickletAmbientLight;
import org.stagemonitor.weather.mock.MockBrickletBarometer;
import org.stagemonitor.weather.mock.MockBrickletHumidity;
import org.stagemonitor.weather.mock.MockBrickletTemperature;

public class WeatherStationPlugin extends StagemonitorPlugin {

	private static final String HOST = "localhost";
	private static final int PORT = 4223;
	private static final String UID = "XYZ";

	private BrickletTemperature brickletTemperature;
	private BrickletHumidity brickletHumidity;
	private BrickletBarometer brickletBarometer;
	private BrickletAmbientLight brickletAmbientLight;
	private IPConnection ipcon;

	@Override
	public void initializePlugin(MetricRegistry metricRegistry, Configuration configuration) throws Exception {
		ipcon = new IPConnection();
//		ipcon.connect(HOST, PORT);
		brickletTemperature = new MockBrickletTemperature(UID, ipcon);
		brickletHumidity = new MockBrickletHumidity(UID, ipcon);
		brickletBarometer = new MockBrickletBarometer(UID, ipcon);
		brickletAmbientLight = new MockBrickletAmbientLight(UID, ipcon);

		ElasticsearchClient.sendGrafanaDashboardAsync("Weather Station.json");

		metricRegistry.register("weather.temp", (Gauge<Double>) this::getTemp);
		metricRegistry.register("weather.humidity", (Gauge<Double>) this::getHumidity);
		metricRegistry.register("weather.mbar", (Gauge<Double>) this::getMbar);
		metricRegistry.register("weather.lux", (Gauge<Double>) this::getLux);

	}

	@Override
	public void onShutDown() {
		try {
			if (ipcon != null) {
				ipcon.disconnect();
			}
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	public double getTemp() {
		try {
			return brickletTemperature.getTemperature() / 100.0;
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}

	public double getHumidity() {
		try {
			return brickletHumidity.getHumidity() / 10.0;
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}

	public double getMbar() {
		try {
			return brickletBarometer.getAirPressure() / 1000.0;
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}

	public double getLux() {
		try {
			return brickletAmbientLight.getIlluminance() / 10.0;
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}

	public static void main(String[] args)throws Exception {
		Stagemonitor.startMonitoring(getMeasurementSession(args));
		System.out.println("Press key to exit"); System.in.read();
		Stagemonitor.shutDown();
	}

	static MeasurementSession getMeasurementSession(String[] args) {
		final CorePlugin corePlugin = Stagemonitor.getConfiguration(CorePlugin.class);
		String applicationName = corePlugin.getApplicationName() != null ? corePlugin.getApplicationName() : "Weather Station";
		String instanceName = corePlugin.getInstanceName() != null ? corePlugin.getInstanceName() : getInstance(args);
		System.out.println(instanceName);
		return new MeasurementSession(applicationName, MeasurementSession.getNameOfLocalHost(), instanceName);
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
		if (args.length > 0) {
			return args[0];
		}
		return "Weather Station";
	}

}
