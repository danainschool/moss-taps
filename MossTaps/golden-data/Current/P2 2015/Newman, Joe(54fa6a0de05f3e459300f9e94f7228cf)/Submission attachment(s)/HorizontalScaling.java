package ravensproject;

import java.util.HashMap;

public class HorizontalScaling extends Transformation {
	String srcWidth, destWidth;
	int scaleFactor;
	static String widthNames[] = { "small", "large", "huge" };

	static private int getScaleFactorFromNames(String fromWidth, String toWidth) {
		return getWidthIndexFromName(toWidth) - getWidthIndexFromName(fromWidth); 
	}
	
	static private String scaleByName(String fromWidthName, int scaleFactor) {
		// find index of from-width
		int fromWidthIndex = getWidthIndexFromName(fromWidthName);
		// apply factor
		// return width at result
		return widthNames[fromWidthIndex + scaleFactor];
	}
	
	private static int getWidthIndexFromName(String fromWidthName) {
		int index = 0;
		for (String widthName : widthNames) {
			if (widthName.equals(fromWidthName)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public HorizontalScaling(String srcWidth, String destWidth) {
		this.srcWidth = srcWidth;
		this.destWidth = destWidth;
		// redundant but convenient
//		this.scaleFactor = getScaleFactorFromNames(srcWidth, destWidth);
	}

	@Override
	public int getScore() {
		return 2;
	}

	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		// given attributes to apply transformation to, and the current Scaling,
		// figure out how much to scale up or down, then 
		attributes.put("width", scaleByName(attributes.get("width"), getScaleFactorFromNames(srcWidth, destWidth)));
		return attributes;
	}
}
