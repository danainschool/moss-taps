package ravensproject;

import java.util.HashMap;

public class Unchanging extends Transformation {
	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		return attributes;
	}

	@Override
	public int getScore() {
		return 5;
	}

}
