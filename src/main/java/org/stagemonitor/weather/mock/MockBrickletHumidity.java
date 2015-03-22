package org.stagemonitor.weather.mock;

import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class MockBrickletHumidity extends BrickletHumidity {

	private int humidity = 400;

	/**
	 * Creates an object with the unique device ID \c uid. and adds it to
	 * the IP Connection \c ipcon.
	 */
	public MockBrickletHumidity(String uid, IPConnection ipcon) {
		super(uid, ipcon);
	}

	@Override
	public int getHumidity() throws TimeoutException, NotConnectedException {
		return humidity = limit(randomWalk(humidity, 10), 300, 600);
	}

	public static int randomWalk(int start, int step) {
		return (int) Math.round(start + (Math.random() - 0.5) * step);
	}

	public static int limit(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
