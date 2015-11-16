/**
 * 
 */
package ravensproject;

/**
 * @author Brett
 *
 */
public class SlotAlignment implements ISlot {

	private String alignment;
	
	public SlotAlignment()
	{
		this.alignment = "";
	}
	
	/* (non-Javadoc)
	 * @see ravensproject.ISlot#Compare(ravensproject.ISlot)
	 */
	@Override
	public Change Compare(ISlot slot) {
		Change result = Change.Changed;
		
		SlotAlignment sa = (SlotAlignment)slot;
		
		String alignment1 = this.alignment;
		String alignment2 = sa.getAlignment(); 
		
		if(alignment1.equalsIgnoreCase("") != true && 
				alignment2.equals("")!= true)
		{
			if(alignment1.equals(alignment2))
		    {	
				result = Change.Unchanged;		    	
		    }
		    else if(alignment1.equals("bottom-right") && alignment2.equals("bottom-left"))
		    {
		    	result = Change.Reflected;		    	
		    }
		    else if(alignment1.equals("top-right") && alignment2.equals("top-left"))
		    {
		    	result = Change.Reflected;		    	
		    }
		    else
		    {	    		
		    	result = Change.Rotated;		    	
		    }
		}
				
		return result;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

}
