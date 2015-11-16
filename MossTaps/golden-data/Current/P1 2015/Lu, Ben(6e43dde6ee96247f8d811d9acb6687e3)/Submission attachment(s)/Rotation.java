package ravensproject;

import java.util.HashMap;

final class Rotation extends Transformation {
	private int angleDifference;
	
	public Rotation(int angleDifference) {
		this.angleDifference = angleDifference;
	}

	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> attributesCopy = (HashMap<String, String>) attributes.clone();
		attributesCopy.put("angle", Utilities.advanceAngleStr(attributes.get("angle"), angleDifference));
		return attributesCopy;
	}

	@Override
	public int getScore() {
		return 3;
	}
	
}