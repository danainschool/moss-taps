package ravensproject;

import java.util.HashMap;

public class HorizontalReflection extends Transformation {
	
	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		String shape = attributes.get("shape");
		String angleString = attributes.get("angle");
		int angle = new Integer(attributes.get("angle"));
		
		if (shape.equals("right triangle")) {
			attributes.put("angle", new Integer(270 - angle).toString());
		} else if (shape.equals("pac-man")) {
			if (angle <= 180) {
				attributes.put("angle", new Integer(180 - angle).toString());
			} else {
				attributes.put("angle", new Integer(540 - angle).toString());
			}
		} else {
			attributes.put("angle", Utilities.advanceAngleStr(angleString, 180));
		}
		return attributes;
	}

	@Override
	public int getScore() {
		return 4;
	}

}
