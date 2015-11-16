package ravensproject;

import java.util.HashMap;

public class OppositeFill extends Transformation {
	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		String newFill = attributes.get("fill").equals("yes") ? "no" : "yes";
		attributes.put("fill", newFill);
		return attributes;
	}

	@Override
	public int getScore() {
		return 4;
	}

}
