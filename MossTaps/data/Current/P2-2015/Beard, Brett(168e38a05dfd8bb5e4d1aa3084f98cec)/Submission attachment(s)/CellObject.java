package ravensproject;

import java.util.HashMap;

public class CellObject {
	   private String name;
	    private HashMap<String, Integer> attributes;
	    
	    /**
	     * Constructs a new CellObject given a name.
	     * 
	     * Your agent does not need to use this method.
	     * 
	     * @param name the name of the object
	     */
	    public CellObject(String name) {
	        this.name=name;
	        attributes=new HashMap<>();
	    }

	    /**
	     * The name of this CellObject. Within a RavensFigure, each CellObject has a unique name.
	     * 
	     * @return the name of the RavensObject
	     */
	    public String getName() {
	        return name;
	    }
	    
	    /**
	     * Returns a HashMap of attributes characterizing this RavensObject. The key
	     * for these attributes is the name of the attribute, and the value is the
	     * value of the attribute. For example, a filled large square would have
	     * attribute pairs "shape:square", "size:large", and "filled:yes".
	     * 
	     * @return a HashMap of name-value attribute pairs.
	     * 
	     */
	    public HashMap<String,Integer> getAttributes() {
	        return attributes;
	    }
	    
	    @Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CellObject))
				return false;
			
			if (obj == this)
				return true;
			
			boolean result = false;
			CellObject rhs = (CellObject) obj;
			
			if(this.attributes.size() == rhs.attributes.size() )
			{
				boolean allEqual = true;
				
				// Loop through each attribute
				for(String key : this.attributes.keySet())
				{
					// Get the attribute value
					int value1 = this.attributes.get(key);
					
					if(key.equals("inside") && value1 == 0)
					{
						continue;
					}
					
					int value2 = Integer.MAX_VALUE;
					
					try {
						value2 = rhs.getAttributes().get(key);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
					if(value1 != value2)
					{
						allEqual = false;
						break;
					}
				}
				
				result = allEqual;				
			}
			
			return result;
	    }
}
