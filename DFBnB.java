import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Stack;

/**
 * This class represents a DFBnB algorithm on a tile puzzle . 
 *
 * @author Moria Maman 
 */
public class DFBnB extends algoHelpFunctions{
	int numOfNodes;
	int total;//total cost
	Hashtable< String,nodeInfo> loopAvoidance_HT;
	Stack<nodeInfo> loopAvoidance_stack;
	int threshold;

	// **************************************************
    // Constructor
    // ************************************************** 
	DFBnB(){

		this.numOfNodes=1;
		this.total=0;
		this.loopAvoidance_HT = new Hashtable<String,nodeInfo>();
		this.loopAvoidance_stack=new Stack<nodeInfo>();
	}

	// **************************************************
    // Public methods
    // **************************************************
	public nodeInfo DFBnB (tileObj [][] startPuzzle, String goal,boolean withOpen) {
		if(fromMatrixToString(startPuzzle).equals(goal)) {//check if the start node is already the goal node
			return new nodeInfo(0,"no path",0,startPuzzle); 
		}
		if(firstCheck(startPuzzle)) {//if one of the black tiles is not in its right position, it means it can't be moved and that there is no solution 
			return new nodeInfo(0,"no path");
		}
		this.threshold=Math.min(Integer.MAX_VALUE, factorial(find_tile_number(startPuzzle)));
		nodeInfo startInf=new nodeInfo(0,"",heuristicFunction(startPuzzle),startPuzzle);
		loopAvoidance_stack.add(startInf);
		String startNodeStr=fromMatrixToString(startPuzzle);
		loopAvoidance_HT.put(startNodeStr,startInf);
		nodeInfo result=new nodeInfo();
		int rounds=1;//for the open list print mode
		while(loopAvoidance_stack.size()>0)
		{
			if(withOpen) {// for the open list print 
				System.out.println("round: "+rounds);//print the number of rounds when print the open list.
				rounds++;
				for(nodeInfo nodeinf : loopAvoidance_stack)
				{
					printNode(nodeinf.getNode());
					System.out.println("*******");
				}
			}	
			nodeInfo node=loopAvoidance_stack.pop();
			if(node.isOut()) {//if the node is marked as out
				loopAvoidance_HT.remove(fromMatrixToString(node.getNode()));
			}
			else {
				node.markAsOut();
				loopAvoidance_stack.push(node);
				
				ArrayList<nodeInfo> operators=new ArrayList<nodeInfo>();
				operators=insertOperators(node);// build array list of all the child operators of the current node
				if(operators.get(0).getPath().equals("no path")) {//if the blank is "black" or dosen't exist in the node matrix.
					return new nodeInfo(0,"no path");
				}
				
				Collections.sort(operators);
				boolean erase=false;

				for(int i=0;i<operators.size() ;i++) {
					String operatorStr=fromMatrixToString(operators.get(i).getNode());
					if(erase==true) {//if the node need to be removed from the array list
						operators.remove(i);
						i--;//after removing element from an arraylist, all elements moves one place left 
						
					}
					else if (operators.get(i).getEvaluation()>=threshold) {
						operators.remove(i);//remove the current child from the array list
						erase=true;//to erase the rest of children that after him in the array list order
						i--;//after removing element from an arraylist, all elements moves one place left 
						
					}

					else if (loopAvoidance_HT.contains(operatorStr)) {
						nodeInfo sameNodeStrInfo=loopAvoidance_HT.get(operatorStr);
						if(sameNodeStrInfo.isOut()==true) {
							operators.remove(i);
							i--;//after removing element from an arraylist, all elements moves one place left 
						}
						else {
							if(sameNodeStrInfo.getEvaluation()<=operators.get(i).getEvaluation())
							{//if the current child node has a bigger Evaluation value, remove it from the array list.
								operators.remove(i);
								i--;//after removing element from an arraylist, all elements moves one place left 
							}
							else {//if the exists node in the hash table has a bigger Evaluation value, remove it.
								loopAvoidance_HT.remove(operatorStr);
								loopAvoidance_stack.remove(sameNodeStrInfo);
							}
						}
					}
					else if(operatorStr.equals(goal)) {
						threshold=operators.get(i).getEvaluation();
						result=operators.get(i);
						erase=true;
						operators.remove(i);
						i--;//after removing element from an arraylist, all elements moves one place left 
						
					}
				}
				Collections.reverse(operators);
				for(int i=0;i<operators.size();i++) {//insert the remains child nodes to the loopAvoidance hash table and stack.
					String operatorStr=fromMatrixToString(operators.get(i).getNode());
					loopAvoidance_HT.put(operatorStr, operators.get(i));
					loopAvoidance_stack.add(operators.get(i));
				}
			}
		}
		return result;
	}

	//**************************************************
    // Private method
	// **************************************************
	/**
	 * this function gets a node and return an array list of all his operator node children
	    * 
	    */
	private ArrayList<nodeInfo> insertOperators(nodeInfo node){
		tileObj [][] nodeMatrix=node.getNode();
		ArrayList<nodeInfo> operatorsList=new ArrayList<nodeInfo>();
		int[] blankIndex=new int[2];//will represents the blank location
		blankIndex=blank_index(nodeMatrix);//find the blank index
		int blankLengthIndex=blankIndex[0];//length index of the "_" 
		int blankWidthIndex=blankIndex[1];//width index of the "_" 
		if(nodeMatrix[blankLengthIndex][blankWidthIndex].color.equals("B")) {//if the blank can not be moved
			operatorsList.add(new nodeInfo(0,"no path"));
			return operatorsList;
		}
		if (blankLengthIndex==-1)//if there is no blank in the puzzle
		{
			operatorsList.add(new nodeInfo(0,"no path"));
			return operatorsList;
		}

		if(blankWidthIndex!=((nodeMatrix[0].length)-1)) {//if left node is possible
			String opposit_operation=nodeMatrix[blankLengthIndex][blankWidthIndex+1].value+"R";//the opposite operation
			if(!(nodeMatrix[blankLengthIndex][blankWidthIndex+1].color.equals("B")) && !opposit_operation.equals(node.getFather())) {//check if the node is not black, and can be moved
				nodeInfo left_node=operatorInfo(node,blankLengthIndex,blankWidthIndex,"L");
				operatorsList.add(left_node);
			}
		}
		if(blankLengthIndex!=nodeMatrix.length-1) {//if up node is possible 
			String opposit_operation=nodeMatrix[blankLengthIndex+1][blankWidthIndex].value+"D";//the opposite operation
			if(!(nodeMatrix[blankLengthIndex+1][blankWidthIndex].color.equals("B")) && !opposit_operation.equals(node.getFather())) {//check if the node is not black, and can be moved
				nodeInfo up_node=operatorInfo(node,blankLengthIndex,blankWidthIndex,"U");
				operatorsList.add(up_node);
			}
		}

		if(blankWidthIndex!=0) {//if right node is possible  
			String opposit_operation=nodeMatrix[blankLengthIndex][blankWidthIndex-1].value+"L";//the opposite operation
			if(!(nodeMatrix[blankLengthIndex][blankWidthIndex-1].color.equals("B")) && !opposit_operation.equals(node.getFather())) {
				nodeInfo right_node=operatorInfo(node,blankLengthIndex,blankWidthIndex,"R");
				operatorsList.add(right_node);
			}
		}
		if(blankLengthIndex!=0) {//if down node is possible 
			String opposit_operation=nodeMatrix[blankLengthIndex-1][blankWidthIndex].value+"U";//the opposite operation
			if(!(nodeMatrix[blankLengthIndex-1][blankWidthIndex].color.equals("B")) && !opposit_operation.equals(node.getFather())) {
				nodeInfo down_node=operatorInfo(node,blankLengthIndex,blankWidthIndex,"D");
				operatorsList.add(down_node);	
			}
		}


		return operatorsList;
	}

	/**
	    * this function crate a new operator child node, with a given string direction value as input("L" for left node child, "U" for up and so on).  
	    * 
	    */
	private nodeInfo operatorInfo(nodeInfo node,int blankLengthIndex,int blankWidthIndex,String direction) {
		this.numOfNodes++;
		tileObj [][] nodeMatrix=node.getNode();
		tileObj [][] operator=childMat(blankLengthIndex,blankWidthIndex,nodeMatrix,direction);//crate the operator child node
		nodeInfo operatorInfo=new nodeInfo();
		String Operation=operator[blankLengthIndex][blankWidthIndex].value+direction;//the node operation ("8D" as example)
		operatorInfo.setFather(Operation);//update the node operation
		operatorInfo.swap(node);//get the path and cost from the father node 
		operatorInfo.copyNode(operator);//insert the operator matrix to the object 
		operatorInfo.addPath("-"+Operation);//update the operator node path
		operatorInfo.addCost(operator[blankLengthIndex][blankWidthIndex].colorPrice);//update the operator node cost  
		operatorInfo.setHeuristicFunResulte(heuristicFunction(operator));//calculate and update the heuristic Function of the node matrix
		return operatorInfo;
	}
	
	
	/**
	    * 
	    * this function gets a matrix that represents a tile puzzle node. 
	    * @return the number of tiles that can be moved .
	    */
	private int find_tile_number(tileObj [][] node) {
		int num=0;
		for(int i=0;i<node.length;i++)
		{
			for(int j=0;j<node[0].length;j++) 
			{
			if (!node[i][j].color.equals("B")) {
				num++;
			}
			}
		}
		return num;
	}
	
	/**
	    * 
	    * this function gets an integer. 
	    * @return the factorial of the number.
	    */
	private static int factorial(int n) {
        int res = 1;

        for (int i = 2; i <= n; i++) {
            res *= i;
        }

        return res;
    }


}
