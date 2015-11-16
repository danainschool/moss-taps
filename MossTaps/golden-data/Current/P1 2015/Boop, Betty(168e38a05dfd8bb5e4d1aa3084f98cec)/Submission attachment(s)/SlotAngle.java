/**
 * 
 */
package ravensproject;

/**
 * @author Brett
 *
 */
public class SlotAngle implements ISlot {
	
	private int angle;
	
	public Change Compare (ISlot slot)
	{
		Change result = Change.Changed;
		
		SlotAngle s = (SlotAngle) slot;		

    	int angle1 = this.angle;
    	int angle2 = s.getAngle();
    	
    	if(angle1 != Integer.MIN_VALUE && angle2 != Integer.MIN_VALUE)
    	{
	    	int difference = angle1 - angle2;
	    	if(difference == 0)
	    	{
	    		result = Change.Unchanged;    		
	    		
	    	} else if(difference == 90 || difference == -90){
	    		
	    		result = Change.Reflected;	    
	    		
	    	} else {
	    		
	    		result = Change.Rotated;	  
	    		
	    	}
    	}
		
		return result;		
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}


}
