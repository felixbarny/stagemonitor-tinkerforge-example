package org.stagemonitor.weather.mock;

import static org.stagemonitor.weather.mock.MockBrickletHumidity.limit;
import static org.stagemonitor.weather.mock.MockBrickletHumidity.randomWalk;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class MockBrickletBarometer extends BrickletBarometer {

	private int mbar = 1_000_000;

	/**
	 * Creates an object with the unique device ID \c uid. and adds it to
	 * the IP Connection \c ipcon.
	 */
	public MockBrickletBarometer(String uid, IPConnection ipcon) {
		super(uid, ipcon);
	}

	@Override
	public int getAirPressure() throws TimeoutException, NotConnectedException {
		return mbar = limit(randomWalk(mbar, 1000), 900_000, 1_100_000);
	}
}
