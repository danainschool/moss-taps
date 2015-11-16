package ravensproject;

import java.util.Hashtable;
import java.util.List;

public class Transformation {	
	
	public Transformation()
	{		
	}
	
	public static Hashtable<String, Change> BuildTransformation(Frame frame1, Frame frame2)
	{
		Hashtable<String, Change> transformation = new Hashtable<String, Change>();
		
		List<FrameObject> list = frame1.getObjectList();
		int list1Size = list.size();
		
		int list2Size = frame2.getObjectList().size();
		
		for(int loopCount = 0; loopCount < Math.min(list1Size, list2Size); loopCount++)
		{
			FrameObject object1 = frame1.getObjectList().get(loopCount);			
			
			if(loopCount < frame2.getObjectList().size())
			{
				FrameObject object2 = frame2.getObjectList().get(loopCount);
				
				Hashtable<String, ISlot> slots = object1.getSlots();
				
				for(String key : slots.keySet())
		    	{
					ISlot slot1 = object1.getSlots().get(key);
					ISlot slot2 = object2.getSlots().get(key);
					if(slot2 != null)
					{
						Change c = slot1.Compare(slot2);
						
						String keyValue = key + "_" + loopCount;
						transformation.put(keyValue, c);
					}			
		    	}
			}
		}
		
		if(list1Size == list2Size)
		{
			transformation.put("num_objects", Change.Unchanged);
		}
		else
		{
			transformation.put("num_objects", Change.Changed);
		}		
		
		return transformation;
	}
	
}
