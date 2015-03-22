package org.stagemonitor.weather.mock;

import static org.stagemonitor.weather.mock.MockBrickletHumidity.limit;
import static org.stagemonitor.weather.mock.MockBrickletHumidity.randomWalk;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class MockBrickletAmbientLight extends BrickletAmbientLight {

	private int lux = 5000;

	/**
	 * Creates an object with the unique device ID \c uid. and adds it to
	 * the IP Connection \c ipcon.
	 */
	public MockBrickletAmbientLight(String uid, IPConnection ipcon) {
		super(uid, ipcon);
	}

	@Override
	public int getIlluminance() throws TimeoutException, NotConnectedException {
		return lux = limit(randomWalk(lux, 10), 3000, 6000);
	}
}
