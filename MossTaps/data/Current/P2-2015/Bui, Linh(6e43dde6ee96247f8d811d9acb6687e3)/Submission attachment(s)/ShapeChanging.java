package ravensproject;

import java.util.HashMap;

public class ShapeChanging extends Transformation {

	String toShape;
	
	public ShapeChanging(String toShape) {
		this.toShape = toShape;
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		attributes.put("shape", toShape);
		return attributes;
	}

}
