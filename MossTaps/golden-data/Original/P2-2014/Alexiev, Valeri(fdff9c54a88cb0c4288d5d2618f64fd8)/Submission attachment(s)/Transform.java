package ravensproject;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Transform {

	double angle = 0;
	boolean flip = false;

	public Transform(){
	}
	
	public Transform(double degrees, boolean fl){
		angle = degrees;
		flip = fl;
	}
	
	public BufferedImage run(BufferedImage img) {
		return null;
	}
	
	public String GetName(){
		return "rotate"+angle + (flip ? "flip" : "");
	}

	public static Transform Identity() {
		return new Transform(){
			@Override
			public BufferedImage run(BufferedImage img){
				return img;
			}
		};
	}

	public static Transform Rotate(double Degrees) {
		return new Transform(Degrees, false){
			@Override
			public BufferedImage run(BufferedImage img){

				AffineTransform tx = new AffineTransform();
				tx.rotate(Math.toRadians(angle), img.getWidth()/2, img.getHeight()/2);
				// TODO: is BILINEAR the right type?
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				BufferedImage rotatedImg = op.filter(img, null);
				
				return rotatedImg;
			}
		};
	}
	
	public static Transform Flip(double Degrees) {
		return new Transform(Degrees, true){
			@Override
			public BufferedImage run(BufferedImage img){
				// Rotate
				AffineTransform tx = new AffineTransform();
				tx.rotate(Math.toRadians(angle), img.getWidth()/2, img.getHeight()/2);
				// TODO: is BILINEAR the right type?
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				BufferedImage rotatedImg = op.filter(img, null);
				
				// Flip
				tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -rotatedImg.getHeight());
				// TODO: is this the right transform type?
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				BufferedImage flippedImg = op.filter(rotatedImg, null);
				
				return flippedImg;
			}
		};
	}
	
	public static java.util.HashMap<java.lang.String, Transform> AllTransforms(){
		java.util.HashMap<java.lang.String, Transform> vec = new java.util.HashMap<java.lang.String, Transform>();
		
		vec.put(Identity().GetName(), Identity());
		vec.put(Rotate(90).GetName(), Rotate(90));
		vec.put(Rotate(180).GetName(), Rotate(180));
		vec.put(Rotate(270).GetName(), Rotate(270));
		vec.put(Flip(0).GetName(), Flip(0));
		vec.put(Flip(90).GetName(), Flip(90));
		vec.put(Flip(180).GetName(), Flip(180));
		vec.put(Flip(270).GetName(), Flip(270));
		
		return vec;
	}
}
