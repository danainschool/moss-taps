package ravensproject;

public class SlotText implements ISlot {

	private String textValue;
	
	public Change Compare (ISlot slot)
	{
		Change result = Change.Changed;
		
		SlotText s = (SlotText) slot;
		String text = s.getTextValue();
		if(text.equals(this.textValue))
		{
			result = Change.Unchanged;
		}
		
		return result;		
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
}
