/**
 * This class represents a single tile puzzle node 
 *
 * @author Moria Maman 
 */


public class nodeInfo implements Comparable<nodeInfo>{
	private tileObj [][] nodeMatrix;//the puzzle node representation 
	private String path;
	private int cost;
	private int heuristicFunResulte;
	private boolean out=false;
	private String father="";
	
    // **************************************************
    // Constructors
    // **************************************************
   
	/**
    * Default constructor.
    */
	nodeInfo(){
		this.path="";
		this.cost=0;
		this.heuristicFunResulte=0;
	}

	  /**
	    * Parameterized constructors.
	    * there are number of different constructors because of the use in different algorithms in the program.
	    */
	nodeInfo (int newCost,String newPath,int huristic,tileObj [][] newNode){
		this.path=newPath;
		this.cost=newCost;
		this.heuristicFunResulte=huristic;
		copyNode(newNode);
	}
	nodeInfo (int newCost,String newPath,int huristic,tileObj [][] newNode,boolean out){
		this.path=newPath;
		this.cost=newCost;
		this.heuristicFunResulte=huristic;
		copyNode(newNode);
		this.out=out;
	}
	nodeInfo (int newCost,String newPath,int huristic){
		this.path=newPath;
		this.cost=newCost;
		this.heuristicFunResulte=huristic;
	}
	nodeInfo (int newCost,String newPath){
		this.path=newPath;
		this.cost=newCost;
	}
	
    // **************************************************
    // Public methods
    // **************************************************
	
	public String getPath() {
		return this.path;
	}
   
	/**
	  * this function gets a new path and add it to the current path of the object.
	 */
	public void addPath(String newPath) {
		this.path=this.path+(newPath);
	} 
	
    public String getFather() {
	    	return this.father;
	   }
	
	public void setFather(String father) {
    	this.father=father;
    }
	
	public int getCost() {
		return this.cost;
	}
	/**
	  * this function gets a new cost value and add it to the current "cost" value of the object.
	 */
	public void addCost (int newCost) {
		this.cost=this.cost+newCost;
	}
	
	
	public int getHeuristicFunResulte() {
		return this.heuristicFunResulte;
	}
	public void setHeuristicFunResulte(int newResult) {
		this.heuristicFunResulte=newResult;
	}
	

	/**
    * this function helps mark the object as out, by changing the "out" value to be true.
    */
	public void markAsOut() {
		this.out=true;
	}
	
	/**
	  * this function helps mark the object as not out, by changing the "out" value to be false.
	 */
	public void setOut() {
		this.out=false;
	}
	/**
	  * this function returns true if the object is marked as out or false otherwise.
	 */
	public boolean isOut(){
		return this.out;
	}
	
	/**
	  * this function calculate and returns the evaluation function value of the current object.
	 */
	public int getEvaluation() {
		return this.heuristicFunResulte+this.cost;
	}
	
	public tileObj [][] getNode(){
		return this.nodeMatrix;
	}
	
	/**
	  * this function sets the node matrix of the current object to another given matrix.
	 */
	public void copyNode(tileObj [][] newNode) {//copy matix function 
		this.nodeMatrix = new tileObj [newNode.length][newNode[0].length] ;
		for(int i=0;i<newNode.length;i++)
		{
			for(int j=0;j<newNode[0].length;j++) {
				this.nodeMatrix[i][j]=new tileObj();
				this.nodeMatrix[i][j].swap(newNode[i][j]);
			}
		}
	}
	
	/**
	  * this function sets the cost and path values of the node to an another given object values.
	 */
	public void swap(nodeInfo newInfo) {
		this.cost=newInfo.getCost();
		this.path=newInfo.getPath();
	}

	/**
	  * this function helps define the compare between two "nodInfo" objects.
	 */
	@Override
	public int compareTo(nodeInfo other) {
		return this.getEvaluation()-other.getEvaluation();
	}


}
