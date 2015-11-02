package ravensproject;

import java.util.HashMap;

public class ObjectDeleted extends Transformation {

	@Override
	public int getScore() {
		return 1;
	}

	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		return null;
	}

}
