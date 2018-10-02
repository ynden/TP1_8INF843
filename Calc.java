package business;

import java.io.Serializable;

public class Calc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9098170725380566933L;

	public int add(String a, String b) {
		int x = Integer.parseInt(a);
		int y = Integer.parseInt(b);

		return x + y;
	}
}