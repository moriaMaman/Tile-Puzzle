/**
 * This class contains some functions for the use of all the algorithms.
 *
 * @author Moria Maman 
 */
public class algoHelpFunctions {

	// **************************************************
	// Private method
	// **************************************************

	/**
	 * this function gets single value and a size of a matrix. 
	 * @return an two size array that represents the location the value should be at in the matrix.
	 */
	private int[] get_Correct_Index(int value,int matLength,int matWidth) {
		int[] index= new int [2];
		int mod= value%matWidth;
		int lengthIndex=value/matWidth;
		int widthIndex=0;
		if(mod==0) {
			lengthIndex=lengthIndex-1;
			widthIndex=matWidth-1;
		}
		else {
			widthIndex=mod-1;

		}
		index[0]=lengthIndex;
		index[1]=widthIndex;
		return index;
	} 

	// **************************************************
	// Public methods
	// **************************************************

	/**
	 * this function gets a matrix that represents a tile puzzle. 
	 * @return a string representation of the matrix.
	 */
	public String fromMatrixToString (tileObj node[][]) {
		String ans="";		
		for(int i=0;i<node.length ;i++)
		{
			for(int j=0;j<node[0].length;j++) {
				if(i==0&&j==0) {
					ans=ans+node[i][j].value;
				}
				else {
					ans=ans+","+node[i][j].value;
				}

			}
		}
		return ans;
	}
	/**
	 * this function gets a matrix that represents a tile puzzle. 
	 * @return true if there is a black tile that is not in its correct position (which means 
	 * it can't be moved.) or false otherwise
	 */
	public Boolean firstCheck (tileObj node[][]) {
		boolean checkAns=false;		
		for(int i=0;i<node.length ;i++)
		{
			for(int j=0;j<node[0].length && checkAns==false;j++) {

				if(node[i][j].color.equals("B")) {//if its a black tile
					if(!node[i][j].value.equals("_")) {//if its not the blank tile
						int numValue=Integer.parseInt(node[i][j].value);
						int [] position=get_Correct_Index(numValue, node.length, node[0].length);//get the correct position the tile shpuld be at
						if(position[0]!=i || position[1]!=j) {//if the tile is black and not in its right position 
							checkAns=true;
						}
					}
					else {//if its the blank tile
						if(i!=node.length-1 || j!=node[0].length-1) {//if the blank tile is black and not in its right position 
							checkAns=true;
						}
					}
				}
			}
		}
		return checkAns;
	}
	/**
	 * this function gets a matrix that represents a tile puzzle. 
	 * @return  an two size array that represents the location of the blank tile.
	 */
	public int[] blank_index(tileObj node[][]) {
		int [] index=new int[2];
		boolean isFound=false;//check if "_" char is found
		for(int i=0;i<node.length &&isFound==false ;i++)//search for the "_" location
		{
			for(int j=0;j<node[0].length ;j++) {
				if(node[i][j].value.equals("_")) {
					index[0]=i;
					index[1]=j;
					isFound=true;
				}
			}
		}
		if(isFound==false) {// if there's no "blank" value in the matrix.
			index[0]=-1;
		}
		return index;
	}


	/**
	 * this function gets a matrix that represents a tile puzzle node. 
	 * @return the node's heuristic function result- by the Manhattan distance formula .
	 */
	public int heuristicFunction(tileObj [][] nodeMat) {
		int result=0;
		for(int i=0;i<nodeMat.length;i++)
		{
			for(int j=0;j<nodeMat[0].length;j++) 
			{
				if(!nodeMat[i][j].value.equals("_")) {// The function does not calculate the blank value result
					int numValue=Integer.parseInt(nodeMat[i][j].value);
					int [] index=get_Correct_Index(numValue,nodeMat.length,nodeMat[0].length);
					int correctLengthIndex=index[0];
					int correctWidthIndex=index[1];
					result=result+(Math.abs(i-correctLengthIndex) + Math.abs(j-correctWidthIndex))*nodeMat[i][j].colorPrice;
				}

			}

		}
		return result;
	}

	/**
	 * this function gets a matrix that represents a tile puzzle node, length and with of the blank index and 
	 * an direction operator("L" for left, "U" for up and so on). 
	 * @return a new child node.
	 */
	public tileObj [][]  childMat(int lenth, int width,tileObj [][] nodeMat,String Operator){
		tileObj [][] child = new tileObj [nodeMat.length][nodeMat[0].length] ;
		for(int i=0;i<nodeMat.length;i++)
		{
			for(int j=0;j<nodeMat[0].length;j++) {
				child[i][j]=new tileObj();
				child[i][j].swap(nodeMat[i][j]);
			}
		}
		if(Operator.equals("L")) {
			tileObj temp=new tileObj();
			temp.swap(child[lenth][width]);
			child[lenth][width].swap(child[lenth][width+1]);
			child[lenth][width+1].swap(temp);
		}
		else if(Operator.equals("U")) {
			tileObj temp=new tileObj();
			temp.swap(child[lenth][width]);
			child[lenth][width].swap(child[lenth+1][width]);
			child[lenth+1][width].swap(temp);
		}
		else if(Operator.equals("R")) {
			tileObj temp=new tileObj();
			temp.swap(child[lenth][width]);
			child[lenth][width].swap(child[lenth][width-1]);
			child[lenth][width-1].swap(temp);
		}
		else{//operator down
			tileObj temp=new tileObj();
			temp.swap(child[lenth][width]);
			child[lenth][width].swap(child[lenth-1][width]);
			child[lenth-1][width].swap(temp);

		}
		return child;

	}

	/**
	 * this function gets a matrix that represents a tile puzzle node and prints it.
	 * 
	 */
	public void printNode(tileObj [][] node){
		for(int i=0;i<node.length;i++)
		{
			String row="";
			for(int j=0;j<node[0].length;j++) {
				row=row+node[i][j].value;
				if(j!=node[0].length-1) {
					row=row+",";
				}
			}
			System.out.println(row);
		}
	}

}
