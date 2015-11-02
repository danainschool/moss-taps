package ravensproject;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageComposition {

	BufferedImage image = null;
	
	public ImageComposition(BufferedImage img){
		image = img;
	}
	
	public BufferedImage run(BufferedImage img2){
		return null;
	}
	
	public static ImageComposition Addition(BufferedImage imgA, BufferedImage imgB){
		BufferedImage img = PixelSubtraction(imgB, imgA);
		return new ImageComposition(img){
			@Override
			public BufferedImage run(BufferedImage img2){
				return PixelAddition(img2, image);
			}
		};
	}
	
	public static ImageComposition Subtraction(BufferedImage imgA, BufferedImage imgB){
		BufferedImage img = PixelSubtraction(imgA, imgB);
		return new ImageComposition(img){
			@Override
			public BufferedImage run(BufferedImage img2){
				return PixelSubtraction(img2, image);
			}
		};
	}
	
	public static ImageComposition BackSubtraction(BufferedImage imgA, BufferedImage imgB){
		BufferedImage img = PixelSubtraction(imgA, imgB);
		return new ImageComposition(img){
			@Override
			public BufferedImage run(BufferedImage img2){
				return PixelAddition(img2, image);
			}
		};
	}
	
	BufferedImage PixelAddition(BufferedImage img1, BufferedImage img2)
	{
        if (img1.getWidth() != img2.getWidth() ||
                img1.getHeight() != img2.getHeight())
        {
            throw new IllegalArgumentException("Dimensions are not the same!");
        }
        BufferedImage img = new BufferedImage(img1.getWidth(), img1.getHeight(),
											BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster r = img.getRaster();
		int[] iArray=new int[1];
		
        for (int y = 0; y < img1.getHeight(); ++y) {
            for (int x = 0; x < img1.getWidth(); ++x) {
                int pixelA = 255 - img1.getData().getSample(x, y, 0);
                int pixelB = 255 - img2.getData().getSample(x, y, 0);
                
                r.getPixel(x, y, iArray);
                if((pixelA + pixelB) < 256){
                	iArray[0] = 255 - (pixelA + pixelB);
                }
                else{
                	iArray[0] = 0;
                }
               	r.setPixel(x, y, iArray);
            }
        }
		
        
        return img;
	}
	
    static BufferedImage PixelSubtraction(BufferedImage img1, BufferedImage img2)
    {
        if (img1.getWidth() != img2.getWidth() ||
                img1.getHeight() != img2.getHeight())
        {
            throw new IllegalArgumentException("Dimensions are not the same!");
        }
        
        BufferedImage img = new BufferedImage(img1.getWidth(), img1.getHeight(),
        									BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster r = img.getRaster();
        int[] iArray=new int[1];
        
        for (int y = 0; y < img1.getHeight(); ++y) {
            for (int x = 0; x < img1.getWidth(); ++x) {
                int pixelA = 255 - img1.getData().getSample(x, y, 0);
                int pixelB = 255 - img2.getData().getSample(x, y, 0);
                
                r.getPixel(x, y, iArray);
                if((pixelA - pixelB) > 0){
                	iArray[0] = 255 - (pixelA - pixelB);
                }
                else{
                	iArray[0] = 255;
                }
               	r.setPixel(x, y, iArray);
            }
        }
               
        return img;
    }
    
    static void SaveImage(BufferedImage img, String name){
    	
    	try {
			ImageIO.write(img, "png", new File("temp\\"+name+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
