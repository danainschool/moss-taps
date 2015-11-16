package ravensproject;

import java.util.ArrayList;
import java.util.List;

public class Frame {
	
	private List<FrameObject> objectList;
	
	public Frame()
	{		
		this.objectList = new ArrayList<FrameObject>();
	}
	
	public static Frame FromFigure(RavensFigure figure)
	{
		Frame frame = new Frame();
		frame.getObjectList().clear();
		
		// Iterate through objects
		for(String objectName : figure.getObjects().keySet()) 
		{
			// Get the Raven object
    		RavensObject thisObject = figure.getObjects().get(objectName);
    		
    		// Build Frame object
    		FrameObject frameObject = new FrameObject(thisObject);    		
    		
    		// Add Frame object to object list
    		frame.getObjectList().add(frameObject);
		}
		
		return frame;
	}

	public List<FrameObject> getObjectList() {
		return objectList;
	}		
}
