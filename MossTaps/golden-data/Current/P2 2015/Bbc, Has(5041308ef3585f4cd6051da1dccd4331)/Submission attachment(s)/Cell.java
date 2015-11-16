package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Cell {
	
	private HashMap<String,CellObject> objects;
	
	public Cell()
	{
		this.objects = new HashMap<>();
	}
	
	public static Cell FromRavensFigure(RavensFigure figure)
	{
		HashMap<String, Integer> objectLookup = new HashMap<>();
		
		Cell cell = new Cell();			
		
		List<String> keys = new ArrayList<String>(figure.getObjects().keySet());
		Collections.sort(keys);
		
		int counter = 1;
		//for(String key: figure.getObjects().keySet()){
		for(String key: keys){
			// Add key to lookup
			objectLookup.put(key, counter);
			
            RavensObject object = figure.getObjects().get(key);            
            
            String cellObjectName = Integer.toString(counter);
            CellObject cellObject = new CellObject(cellObjectName);            
            for(String attributeKey : object.getAttributes().keySet())
            {
            	String value = object.getAttributes().get(attributeKey);
            	
            	int attributeValue = 0;
            	if(attributeKey.equals("shape"))
            	{            		
            		attributeValue = GetShapeValue(value);
            		cellObject.getAttributes().put(attributeKey, attributeValue);
            	}
            	else if(attributeKey.equals("fill"))
            	{            		
            		attributeValue = GetFillValue(value);
            		cellObject.getAttributes().put(attributeKey, attributeValue);
            	}
            	else if(attributeKey.equals("size"))
            	{            		
            		attributeValue = GetSizeValue(value);
            		cellObject.getAttributes().put("width", attributeValue);
            		cellObject.getAttributes().put("height", attributeValue);
            	}
            	else if(attributeKey.equals("width") || attributeKey.equals("height"))
            	{            		
            		attributeValue = GetSizeValue(value);
            		cellObject.getAttributes().put(attributeKey, attributeValue);
            	}
            	else if(attributeKey.equals("inside"))
            	{            		
            		String[] tokens = value.split(",");
                	for (String token : tokens) 
                	{
                		try {
							attributeValue = objectLookup.get(token.trim());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}                   		
                	}   
                	cellObject.getAttributes().put(attributeKey, attributeValue);
            	}
            }
            
            cell.getObjects().put(cellObjectName, cellObject);
            
            counter++;
        }
		
		return cell;
	}
	
	public static int GetShapeValue(String shapeText)
	{
		int value = 0;
		
		if(shapeText.equals("square"))
		{
			value = 1;
		}
		else if(shapeText.equals("circle"))
		{
			value = 2;
		}
		else if(shapeText.equals("octagon"))
		{
			value = 3;
		}
		else if(shapeText.equals("diamond"))
		{
			value = 4;
		}
		else if(shapeText.equals("star"))
		{
			value = 4;
		}
		else if(shapeText.equals("rectangle"))
		{
			value = 5;
		}
		else if(shapeText.equals("triangle"))
		{
			value = 6;
		}
		else if(shapeText.equals("right triangle"))
		{
			value = 7;
		}
		else
		{			
			System.out.format("Unknown shape: %s%n", shapeText);
		}
		
		return value;
	}

	public static int GetFillValue(String text)
	{
		int value = 0;
		
		if(text.equals("no"))
		{
			value = 1;
		}
		else if(text.equals("yes"))
		{
			value = 2;
		}		
		else
		{			
			System.out.format("Unknown Fill: %s%n", text);
		}
		
		return value;
	}
	
	public static int GetSizeValue(String text)
	{
		int value = 0;
		
		if(text.equals("very small"))
		{
			value = 1;
		}
		else if(text.equals("small"))
		{
			value = 2;
		}
		else if(text.equals("medium"))
		{
			value = 3;
		}
		else if(text.equals("large"))
		{
			value = 4;
		}
		else if(text.equals("very large"))
		{
			value = 5;
		}
		else if(text.equals("huge"))
		{
			value = 6;
		}
		else
		{
			System.out.format("Unknown size: %s%n", text);
		}
		
		return value;
	}

	public int getObjectCount() {
		return this.objects.size();
	}

	
	public HashMap<String,CellObject> getObjects() {
        return this.objects;
    }	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cell))
			return false;
		
		if (obj == this)
			return true;

		boolean result = false;
		Cell rhs = (Cell) obj;
		if(this.getObjectCount() == rhs.getObjectCount()) {
			
			boolean allEqual = true;
			for(String key : this.objects.keySet())
			{
				CellObject cellObject1 = this.objects.get(key);
				
				CellObject cellObject2 = rhs.getObjects().get(key);
				if(cellObject2 != null)
				{
					if(cellObject1.equals(cellObject2) != true)
					{
						allEqual = false;
						break;
					}
				}
				else
				{
					allEqual = false;
				}
				
			}
			result = allEqual;
		}
		return result;
	}
}
