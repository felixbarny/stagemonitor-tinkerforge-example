package org.stagemonitor.weather.mock;

import static org.stagemonitor.weather.mock.MockBrickletHumidity.limit;
import static org.stagemonitor.weather.mock.MockBrickletHumidity.randomWalk;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class MockBrickletTemperature extends BrickletTemperature {

	private short temp = 2_100;

	/**
	 * Creates an object with the unique device ID \c uid. and adds it to
	 * the IP Connection \c ipcon.
	 */
	public MockBrickletTemperature(String uid, IPConnection ipcon) {
		super(uid, ipcon);
	}

	@Override
	public short getTemperature() throws TimeoutException, NotConnectedException {
		return temp = (short) limit(randomWalk(temp, 10), 1_800, 2_800);
	}
}
