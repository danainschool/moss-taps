package ravensproject;

// Uncomment these lines to access image processing.
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.util.HashMap;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
    	
    	boolean bigProblem = problem.getProblemType().contains("3x3");
    	
    	BufferedImage img = null;
    	BufferedImage img2 = null;
    	BufferedImage img3 = null;
    	
    	if(bigProblem)
    	{
    		img = GetImage(problem, "A");
        	img2 = GetImage(problem, "C");
        	img3 = GetImage(problem, "G");
    	}
    	else{
        	img = GetImage(problem, "A");
        	img2 = GetImage(problem, "B");
        	img3 = GetImage(problem, "C");
    	}
    	
    	System.out.println(problem.getName());
    	
    	// TODO: VVA, can we put this in the constructor? I think so.
    	HashMap<java.lang.String, Transform> allTransforms = Transform.AllTransforms();
    	HashMap<java.lang.String, BufferedImage> transforms = new HashMap<java.lang.String, BufferedImage>();
    	
    	BestFitResult res = new BestFitResult();
    	
    	// Starting with A
    	for(String t : allTransforms.keySet()){
    		BufferedImage txImg = allTransforms.get(t).run(img);
    		transforms.put(t, txImg);
    	}
    	
    	// A:B :: C:? or A:C :: G:?
    	String imgName = bigProblem ? "G" : "C";
    	BestFitResult temp = FindBestFit(transforms, allTransforms, img2, imgName, res.maxS );
    	if(temp.maxS > res.maxS)
    		res = temp;
    	
    	// A:C :: B:? or A:G :: C:?
    	imgName = bigProblem ? "C" : "B";
    	temp = FindBestFit(transforms, allTransforms, img3, imgName, res.maxS);
    	if(temp.maxS > res.maxS)
    		res = temp;
    	
    	if(bigProblem){			
    		// Starting with E
    		transforms = new HashMap<java.lang.String, BufferedImage>();
        	for(String t : allTransforms.keySet()){
        		BufferedImage txImg = allTransforms.get(t).run(GetImage(problem, "E"));
        		transforms.put(t, txImg);
        	}
        	
        	// E:F :: H:?
        	temp = FindBestFit(transforms, allTransforms, GetImage(problem, "F"), "H", res.maxS);
        	if(temp.maxS > res.maxS)
        		res = temp;
        	
        	// E:H :: F:?
        	temp = FindBestFit(transforms, allTransforms, GetImage(problem, "H"), "F", res.maxS);
        	if(temp.maxS > res.maxS)
        		res = temp;
    	}
    	
    	
    	// Create list of answers
		java.util.HashMap<java.lang.String, Double> scores = new java.util.HashMap<java.lang.String, Double>();
		scores.put("1", Double.MIN_VALUE);
		scores.put("2", Double.MIN_VALUE);
		scores.put("3", Double.MIN_VALUE);
		scores.put("4", Double.MIN_VALUE);
		scores.put("5", Double.MIN_VALUE);
		scores.put("6", Double.MIN_VALUE);
		if(bigProblem){
			scores.put("7", Double.MIN_VALUE);
			scores.put("8", Double.MIN_VALUE);
		}
		
		// Go through list
		Double max = Double.MIN_VALUE;
		String ans = "-1";
		
		BufferedImage transformed = GetImage(problem, res.imgToTransform);
		transformed = res.tx.run(transformed);
		if(res.comp != null)
			transformed = res.comp.run(transformed);
				
		for(String name: scores.keySet()){
			
			BufferedImage answer = GetImage(problem, name);
			
			double tmp = TverskySimilarity(transformed, answer);
			
			if(tmp > max)
			{
				System.out.println("New answer, new min " + name + "; " + tmp);
				max = tmp;
				ans = name;
			}
		}

    	return Integer.parseInt(ans);
    }
    
    class BestFitResult{
    	public Transform tx = null;
    	public ImageComposition comp = null;
    	public String imgToTransform = null;
    	public Double maxS = -1.0;
    }
    
    BestFitResult FindBestFit(HashMap<java.lang.String, BufferedImage> transforms,
    							HashMap<java.lang.String, Transform> allTransforms,
    							BufferedImage img, String imgToTransform, Double maxS)
    {
    	BestFitResult res = new BestFitResult();
    	res.maxS = maxS;
    	for(String t : transforms.keySet()){
    		BufferedImage txImg = transforms.get(t);
    		double temp = TverskySimilarity(txImg, img);
			if(temp > res.maxS)
			{
				System.out.println("Found a closer transform to apply to " + imgToTransform + ": " + t + " : " + temp);
				res.maxS = temp;
				res.tx = allTransforms.get(t);
				res.imgToTransform = imgToTransform;
				
				double sAB = PixelDifference(txImg, img);
				//System.out.println("PixelDifference(A-C) is " + sAB);
				double sBA = PixelDifference(img, txImg);
				if(sAB == 0 && sBA == 0){
					res.comp = null;
					//System.out.println("ImageComposition = null (1)");
				}
				else{
					//double sBA = PixelDifference(img3, txImg);
					if(sAB == sBA){
						res.comp = ImageComposition.Addition(txImg, img);
						//System.out.println("ImageComposition.Addition");
					}
					else if(sAB > sBA){
						res.comp = ImageComposition.Subtraction(txImg, img);
						//System.out.println("ImageComposition.Subtraction");
					}
					else if(sAB < sBA){
						res.comp = ImageComposition.Addition(txImg, img);
						//System.out.println("ImageComposition.Addition (2)");
					}
					else{
						res.comp = null; 
						//System.out.println("ImageComposition = null (2)");
					}
				}
			}
    	}
    	return res;
    }
    
    long PixelDifference(BufferedImage img1, BufferedImage img2)
    {
        if (img1.getWidth() != img2.getWidth() ||
                img1.getHeight() != img2.getHeight())
        {
            throw new IllegalArgumentException("Dimensions are not the same!");
        }
        
        long diff = 0;
        
        for (int y = 0; y < img1.getHeight(); ++y) {
            for (int x = 0; x < img1.getWidth(); ++x) {
                int pixelA = 255 - img1.getData().getSample(x, y, 0);
                int pixelB = 255 - img2.getData().getSample(x, y, 0);
                
                if((pixelA - pixelB) > 0)
                	diff += (pixelA - pixelB);
            }
        }
        
        
        return diff;
    }
    
    double TverskySimilarity(BufferedImage img1, BufferedImage img2)
    {
        if (img1.getWidth() != img2.getWidth() ||
                img1.getHeight() != img2.getHeight())
        {
            throw new IllegalArgumentException("Dimensions are not the same!");
        }
        
    	double sim = 0;
    	
    	double intersection = 0;
    	double union = 0;
    	
        for (int y = 0; y < img1.getHeight(); ++y) {
            for (int x = 0; x < img1.getWidth(); ++x) {
               int pixelA = 255 - img1.getData().getSample(x, y, 0);
               int pixelB = 255 - img2.getData().getSample(x, y, 0);
               
               intersection += Math.min(pixelA, pixelB);
               union += Math.max(pixelA, pixelB);
            }
        }
    	sim = intersection / union;
    	
    	return sim;
    }
    
    BufferedImage GetImage(RavensProblem problem, String name){
    	BufferedImage img = null;
    	try {
			BufferedImage temp = ImageIO.read(new File(problem.getFigures().get(name).getVisual()));
	        img = new BufferedImage(temp.getWidth(), temp.getHeight(),
	                    BufferedImage.TYPE_BYTE_GRAY);
	
	        Graphics2D g = img.createGraphics();
	        g.drawImage(temp, 0, 0, null);
	        g.dispose();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return img;
    }
    
    double RootMeanSquare(BufferedImage image1, BufferedImage image2){
    	double rms = 0;
    	
        if (image1.getWidth() != image2.getWidth() ||
                image1.getHeight() != image2.getHeight())
        {
            throw new IllegalArgumentException("Dimensions are not the same!");
        }
    	
        for (int y = 0; y < image1.getHeight(); ++y) {
            for (int x = 0; x < image1.getWidth(); ++x) {
               int pixelA = image1.getRGB(x, y);
               int pixelB = image2.getRGB(x, y);
               
               rms += Math.pow(pixelA - pixelB, 2);               
            }
        }
        
        rms = Math.sqrt(rms / (image1.getHeight() * image1.getWidth()));
    	
    	return rms;
    }
    
    void SaveImage(BufferedImage img, String name){
    	
    	try {
			ImageIO.write(img, "png", new File("temp\\"+name+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
