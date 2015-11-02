package ravensproject;

import java.util.HashMap;

public class VerticalReflection extends Transformation {
	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		String shape = attributes.get("shape");
		
		String alignment = attributes.get("alignment");
		if (alignment != null) {
			String newAlignment;
			
			switch (alignment) {
			case "top-left":
				newAlignment = "bottom-left";
				break;
			case "bottom-left":
				newAlignment = "top-left";
				break;
			case "top-right":
				newAlignment = "bottom-right";
				break;
			case "bottom-right":
				newAlignment = "top-right";
				break;
			default:
				newAlignment = "invalid alignment";
				break;
			}
			attributes.put("alignment", newAlignment);
		}
		
		if (Utilities.shapesUnchangedByAngle.contains(shape)) {
			return attributes;
		} else {			
			String angleString = attributes.get("angle");
			int angle = new Integer(attributes.get("angle"));

			if (shape.equals("right triangle")) {
				if (angle <= 90) {
					attributes.put("angle", new Integer(90 - angle).toString());
				} else {
					attributes.put("angle", new Integer(450 - angle).toString());
				}
			} else if (shape.equals("pac-man")) {
				attributes.put("angle", new Integer(360 - angle).toString());
			} else {
				attributes.put("angle", Utilities.advanceAngleStr(angleString, 90));
			}
		}
		return attributes;
	}

	@Override
	public int getScore() {
		return 4;
	}

}
