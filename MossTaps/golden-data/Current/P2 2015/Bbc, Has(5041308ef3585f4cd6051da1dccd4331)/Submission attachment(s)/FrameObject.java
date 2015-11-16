/**
 * 
 */
package ravensproject;

import java.util.Hashtable;

/**
 * @author Brett
 *
 */
public class FrameObject {

	private String name;
	
	private Hashtable<String, ISlot> slots;	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Hashtable<String, ISlot> getSlots() {
		return slots;
	}
	
	public FrameObject()
	{
		this.Init();
	}
	
	public FrameObject(RavensObject ravenObject)
	{
		this.Init();
		
		this.name = ravenObject.getName();
		String shapeText = "";
		
		for(String attributeName : ravenObject.getAttributes().keySet()) {
			
			String attributeValue = ravenObject.getAttributes().get(attributeName);
			
			if(attributeName.equalsIgnoreCase("angle"))
			{
				SlotAngle slot = new SlotAngle();
				slot.setAngle(Integer.parseInt(attributeValue));
				this.getSlots().put(attributeName, slot);
			}
			else if(attributeName.equalsIgnoreCase("alignment"))
			{
				SlotText text = new SlotText();
				text.setTextValue(attributeValue);
				this.getSlots().put(attributeName, text);
				
				SlotAlignment slot = new SlotAlignment();
				slot.setAlignment(attributeValue);				
				this.getSlots().put("alignment_diff", slot);
			}
			else if(attributeName.equalsIgnoreCase("above"))
			{
				// Ignore
			}
			else if(attributeName.equalsIgnoreCase("inside"))
			{				
				if(attributeValue.length() == 1)
				{
					SlotText slot = new SlotText();
					slot.setTextValue(shapeText);
					this.getSlots().put(attributeName, slot);
				}
			}
			else
			{
				if(attributeName.equalsIgnoreCase("shape"))
				{
					shapeText = attributeValue;
				}
				
				SlotText slot = new SlotText();
				slot.setTextValue(attributeValue);
				this.getSlots().put(attributeName, slot);
			}
			
		}
	}
	
	private void Init()
	{
		this.slots = new Hashtable<String, ISlot>();
	}	
}
