package ravensproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AttributeComparator {
	private HashMap<String, String> srcAttrs;
	private HashMap<String, String> destAttrs;

	static List<String> relationshipAttrs = Arrays.asList("inside", "above", "left-of", "overlaps");
	static List<String> relationshipAttrsWithoutPosition = Arrays.asList("inside");
	static List<String> shapesUnchangedByAngle = Arrays.asList(new String[] { "circle", "square", "diamond", "octagon" });

	public AttributeComparator(HashMap<String, String> srcAttrs, HashMap<String, String> destAttrs) {
		super();
		this.srcAttrs = srcAttrs;
		this.destAttrs = destAttrs;
	}

	boolean areChangedShapes() {
		return srcAttrs.get("shape") != null && destAttrs.get("shape") != null
				&& !srcAttrs.get("shape").equals(destAttrs.get("shape"));
	}

	boolean bothAttrsHaveSameOutboundRelationship() {
		for (String relationship : relationshipAttrs) {
			if (srcAttrs.containsKey(relationship) && 
				destAttrs.containsKey(relationship) && 
				srcAttrs.get(relationship).split(",").length == destAttrs.get(relationship).split(",").length)
				return true;
		}
		return false;
	}

	public boolean areHorizontallyScaled() {
		return srcAttrs.get("width") != null && destAttrs.get("width") != null
				&& !srcAttrs.get("width").equals(destAttrs.get("width"));
	}

	public boolean areEqualAttributeHashes(String ignoredAttributes[]) {
		boolean unchanged = true;
		// if all attributes match, relationship = 'unchanged'
		for (String attr : srcAttrs.keySet()) {
			// ignore attributes that refer to other objects, because they don't
			// help with determining transformations
			List<String> ignoredAttrsList = new ArrayList<>(Arrays.asList(ignoredAttributes));
			ignoredAttrsList.addAll(relationshipAttrs);
			if (ignoredAttrsList.contains(attr)) {
				continue;
			} else if (attr.equals("angle") && destAttrs.get("angle") != null) {
				// all attributes have to match, except angles, which have to be
				// equal mod 360
				unchanged = Utilities.getAngleDifference(srcAttrs, destAttrs) % 360 == 0;
			} else if (!srcAttrs.get(attr).equals(destAttrs.get(attr))) {
				unchanged = false;
			}
		}
		return unchanged;

	}

	public boolean areUnchanged() {
		if (areEquivalentShapes()) {
			return areEqualAttributeHashes(new String[] { "angle", "shape", "size", "width", "height" });
		}
		// some shapes are the same no matter the angle
		else if (shapesUnchangedByAngle.contains(srcAttrs.get("shape"))) {
			return areEqualAttributeHashes(new String[] { "angle" });
		} else {
			return areEqualAttributeHashes(new String[] {});
		}
	}

	public boolean areEquivalentShapes() {
		return areEquivalentSquares();
	}

	public boolean areEquivalentSquares() {
		// if one is a rectangle, the other is a square, and the former's width
		// and height
		// are equal and the same as the latter's size, then they're equivalent
		return areSquaresIn1Direction(srcAttrs, destAttrs) || areSquaresIn1Direction(destAttrs, srcAttrs);
	}

	public boolean areSquaresIn1Direction(HashMap<String, String> srcAttrs2, HashMap<String, String> destAttrs2) {
		return srcAttrs2.get("shape").equals("rectangle") && destAttrs2.get("shape").equals("square")
				&& srcAttrs2.get("width").equals(srcAttrs.get("height"))
				&& srcAttrs2.get("width").equals(destAttrs.get("size"));
	}

	public boolean areHorizontallyReflected() {
		if (srcAttrs.get("angle") == null || destAttrs.get("angle") == null
				|| !srcAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}
		// TODO handle alignment
		String shape = srcAttrs.get("shape");
		if (shape.equals("right triangle")) {
			return Utilities.getAngleSum(destAttrs, srcAttrs) == 270;
		} else if (shape.equals("pac-man")) {
			int angle = new Integer(srcAttrs.get("angle"));
			if (angle <= 180) {
				return Utilities.getAngleSum(destAttrs, srcAttrs) == 180;
			} else {
				return Utilities.getAngleSum(destAttrs, srcAttrs) == 540;
			}
		} else if (shape.equals("circle") || shape.equals("square")) {
			return true;
		} else {
			return false;
		}
	}

	boolean areVerticallyReflected() {
		if (srcAttrs.get("angle") == null || destAttrs.get("angle") == null
				|| !srcAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}

		String srcAlignment = srcAttrs.get("alignment");
		String destAlignment = destAttrs.get("alignment");
		// only way to pass is for one of these 4 conditions to apply
		if (srcAlignment != null
				&& destAlignment != null
				&& (!(srcAlignment.equals("top-left") && destAlignment.equals("bottom-left"))
						&& !(srcAlignment.equals("bottom-left") && destAlignment.equals("top-left"))
						&& !(srcAlignment.equals("top-right") && destAlignment.equals("bottom-right")) && !(srcAlignment
						.equals("bottom-right") && destAlignment.equals("top-right")))) {
			return false;
		}

		String shape = srcAttrs.get("shape");
		if (shape.equals("right triangle")) {
			return Utilities.getAngleSum(destAttrs, srcAttrs) % 360 == 90;
		} else if (shape.equals("pac-man")) {
			return Utilities.getAngleSum(destAttrs, srcAttrs) % 360 == 0;
		} else if (shape.equals("circle") || shape.equals("square")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean areOppositeFill() {
		String sourceFill = srcAttrs.get("fill");
		if (sourceFill == null || destAttrs.get("fill") == null
				|| !srcAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}
		return !sourceFill.equals(destAttrs.get("fill"));
	}

	public boolean areRotated() {
		return srcAttrs.get("angle") != null && destAttrs.get("angle") != null
				&& srcAttrs.get("shape").equals(destAttrs.get("shape"))
				&& !srcAttrs.get("angle").equals(destAttrs.get("angle"));
	}

	public boolean areScaled() {
		String srcSize = srcAttrs.get("size");
		String destSize = destAttrs.get("size");
		if (srcSize == null || destSize == null || !srcAttrs.get("shape").equals(destAttrs.get("shape"))) {
			return false;
		}
		return !srcSize.equals(destSize);
	}
}
