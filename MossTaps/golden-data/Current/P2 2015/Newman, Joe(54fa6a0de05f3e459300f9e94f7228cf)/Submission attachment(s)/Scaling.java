package ravensproject;

import java.util.HashMap;

public class Scaling extends Transformation {
	String srcSize, destSize;
	int scaleFactor;
	static String sizeNames[] = { "very small", "small", "medium", "large", "very large", "huge" };

	static private int getScaleFactorFromNames(String fromSize, String toSize) {
		return getSizeIndexFromName(toSize) - getSizeIndexFromName(fromSize); 
	}
	
	static private String scaleByName(String fromSizeName, int scaleFactor) {
		// find index of from-size
		int fromSizeIndex = getSizeIndexFromName(fromSizeName);
		// apply factor
		// return size at result
		return sizeNames[fromSizeIndex + scaleFactor];
	}
	
	private static int getSizeIndexFromName(String fromSizeName) {
		int index = 0;
		for (String sizeName : sizeNames) {
			if (sizeName.equals(fromSizeName)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public Scaling(String srcSize, String destSize) {
		this.srcSize = srcSize;
		this.destSize = destSize;
		// redundant but convenient
//		this.scaleFactor = getScaleFactorFromNames(srcSize, destSize);
	}

	@Override
	public int getScore() {
		return 2;
	}

	@Override
	public HashMap<String, String> apply(HashMap<String, String> attributes) {
		// given attributes to apply transformation to, and the current Scaling,
		// figure out how much to scale up or down, then 
		attributes.put("size", scaleByName(attributes.get("size"), getScaleFactorFromNames(srcSize, destSize)));
		return attributes;
	}

}
