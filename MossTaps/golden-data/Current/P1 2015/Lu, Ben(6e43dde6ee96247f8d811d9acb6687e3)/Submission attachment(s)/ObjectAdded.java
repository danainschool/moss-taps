package ravensproject;

import java.util.HashMap;

public class ObjectAdded extends Transformation {

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		return attributes;
	}

}
