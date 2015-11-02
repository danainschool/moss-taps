package ravensproject;

import java.util.HashMap;

abstract class Transformation {
	public abstract int getScore();

	public abstract HashMap<String, String> apply(
			HashMap<String, String> attributes);
}