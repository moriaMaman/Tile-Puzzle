/**
 * This class represents a single tile in the puzzle  
 *
 * @author Moria Maman 
 */
public class tileObj {
	public String value;
	public String color;
	public int colorPrice;
	
    // **************************************************
    // Constructors
    // **************************************************
	
	/**
	    * Default constructor.
	    */
	tileObj(){
		this.value="";
		this.color="";
		this.colorPrice=0;
		
	}
	
	/**
	  * Parameterized constructors.
	 */
	tileObj(String newVal, String newColor,int colorPrice){
		this.value=newVal;
		this.color=newColor;
		this.colorPrice=colorPrice;
	}
	
    // **************************************************
    // Public methods
    // **************************************************
	
	public void setValue(String newVal) {
		this.value=newVal;
		
	}
	public void setColor(String newColor) {
		this.color=newColor;
		
	}
	public void setPrice(int newPrice) {
		this.colorPrice=newPrice;
		
	}
	public void swap(tileObj newObj) {
		this.value=newObj.value;
		this.color=newObj.color;
		this.colorPrice=newObj.colorPrice;
	
	}

}
