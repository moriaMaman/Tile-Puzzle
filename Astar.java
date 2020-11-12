
import java.util.Hashtable;
import java.util.PriorityQueue;
/**
 * This class represents a A* algorithm on a tile puzzle . 
 *
 * @author Moria Maman 
 */
public class Astar extends algoHelpFunctions {
	
	int numOfNodes;
	int total;//total cost
	Hashtable< String,nodeInfo> frontier_HT;
	Hashtable<String,nodeInfo> exploreSet ;
	PriorityQueue<nodeInfo> frontier_Q ;
	
	// **************************************************
    // Constructor
    // ************************************************** 
	Astar (){
	    numOfNodes=1;
		total=0;
	    frontier_HT = new Hashtable<String,nodeInfo>();
		exploreSet = new Hashtable<String,nodeInfo>();
		frontier_Q = new PriorityQueue<>();
	}
	
	// **************************************************
    // Public methods
    // **************************************************
	public nodeInfo Astar (tileObj [][] startPuzzle, String goal,Boolean withOpen){
		if(fromMatrixToString(startPuzzle).equals(goal)) {//check if the start node is already the goal node
			return new nodeInfo(0,"no path",0,startPuzzle); 
		}
		if(firstCheck(startPuzzle)) {//if one of the black tiles is not in its right position, it means it can't be moved and that there is no solution 
			return new nodeInfo(0,"no path");
		}
		int heuristicStartValue=heuristicFunction(startPuzzle);
		nodeInfo StartNode=new nodeInfo(0,"",heuristicStartValue,startPuzzle);
		frontier_Q.add(StartNode);
		String startNodeStr=fromMatrixToString(startPuzzle);
		frontier_HT.put(startNodeStr,StartNode);
		int rounds=1;//for the open list print mode
		while(frontier_HT.size()>0) {
			if(withOpen) {// for the open list print 
				System.out.println("round: "+rounds);//print the number of rounds when print the open list.
				rounds++;
				for(nodeInfo nodeinf : frontier_Q)
			    {
					printNode(nodeinf.getNode());
					System.out.println("*******");
				}
			}
			nodeInfo node= frontier_Q.remove();
			tileObj [][] nodeMatrix=node.getNode();
			String nodeStr=fromMatrixToString(nodeMatrix);
			if(nodeStr.equals(goal)) {
				return node;
			}
			nodeInfo node_info= new nodeInfo();
			node_info=frontier_HT.get(nodeStr);
			exploreSet.put(nodeStr,node_info);//get in the close list
			frontier_HT.remove(nodeStr);//remove node from the open list
			int[] blankIndex=new int[2];//will represents the blank location
			blankIndex=blank_index(nodeMatrix);//find the blank index
			int blankLengthIndex=blankIndex[0];//length index of the "_" 
			int blankWidthIndex=blankIndex[1];//width index of the "_" 
			if(nodeMatrix[blankLengthIndex][blankWidthIndex].color.equals("B"))  return new nodeInfo(0,"no path");//if the blank can not be moved
			if (blankLengthIndex==-1) return new nodeInfo(0,"no path");//if there is no blank in the node.

			if(blankWidthIndex!=((nodeMatrix[0].length)-1)) {//if left node is possible
				String operation=nodeMatrix[blankLengthIndex][blankWidthIndex+1].value+"R";//the opposite operation
				if(!(nodeMatrix[blankLengthIndex][blankWidthIndex+1].color.equals("B")) && !operation.equals(node.getFather())) {//check if the node is not black, and can be moved
					this.help(nodeMatrix,blankLengthIndex,blankWidthIndex,"L");
				}
			}
			if(blankLengthIndex!=nodeMatrix.length-1) {//if up node is possible 
				String operation=nodeMatrix[blankLengthIndex+1][blankWidthIndex].value+"D";//the opposite operation
				if(!(nodeMatrix[blankLengthIndex+1][blankWidthIndex].color.equals("B"))  && !operation.equals(node.getFather())) {//check if the node is not black, and can be moved
					this.help(nodeMatrix,blankLengthIndex,blankWidthIndex,"U");
				}
			}
			if(blankWidthIndex!=0) {//if right node is possible 
				String operation=nodeMatrix[blankLengthIndex][blankWidthIndex-1].value+"L";//the opposite operation
				if(!(nodeMatrix[blankLengthIndex][blankWidthIndex-1].color.equals("B"))  && !operation.equals(node.getFather())) {//check if the node is not black, and can be moved
					this.help(nodeMatrix,blankLengthIndex,blankWidthIndex,"R");
				}
			}
			if(blankLengthIndex!=0) {//if down node is possible 
				String operation=nodeMatrix[blankLengthIndex-1][blankWidthIndex].value+"U";//the opposite operation
				if(!(nodeMatrix[blankLengthIndex-1][blankWidthIndex].color.equals("B"))  && !operation.equals(node.getFather())) {//check if the node is not black, and can be moved
					this.help(nodeMatrix,blankLengthIndex,blankWidthIndex,"D");
				}
			}

		}
		return new nodeInfo(0,"no path"); 
	}

	//**************************************************
	// Private method
	// **************************************************
	
	/**
	    * this function crate a new operator child node, with a given string direction value as input("L" for left node child, "U" for up and so on). 
	    * the function check if the new node is not in the open list and not in the close list, and helps update its information. 
	    * 
	    */
	private void help(tileObj [][] nodeMatrix, int blankLengthIndex ,int blankWidthIndex ,String direction) {
		this.numOfNodes++;
		tileObj [][] nodeOperator=childMat(blankLengthIndex,blankWidthIndex,nodeMatrix,direction);//crate the operator child node
		String nodeStr= fromMatrixToString(nodeMatrix);
		String operatorNodeStr=fromMatrixToString(nodeOperator);
		nodeInfo newInfo=new nodeInfo();
		String operation=nodeOperator[blankLengthIndex][blankWidthIndex].value+direction;//the node operation ("8D" as example)
		newInfo.setFather(operation);//update the node operation
		newInfo.swap(exploreSet.get(nodeStr));//get the path and cost from the father node 
		String newPath="-"+operation;//make new string of the new operator child operation
		newInfo.addPath(newPath);//add the new operation to the child path
		newInfo.addCost(nodeOperator[blankLengthIndex][blankWidthIndex].colorPrice);//add the cost of the movement of the new operation
		int result=heuristicFunction(nodeOperator);//calculate the heuristic function of the operator node
		newInfo.setHeuristicFunResulte(result);//add the heuristic function value  
		newInfo.copyNode(nodeOperator);// add the operator node matrix
		if(!frontier_HT.containsKey(operatorNodeStr) && !exploreSet.containsKey(operatorNodeStr)) {//check if the matrix node is not in the open list and not in the close list 
			frontier_HT.put(operatorNodeStr,newInfo);
			frontier_Q.add(newInfo);

		}
		else if (frontier_HT.containsKey(operatorNodeStr)){
			int evaluation=frontier_HT.get(operatorNodeStr).getEvaluation();
			if(newInfo.getEvaluation()<evaluation) {// if the exists node in the open list has a bigger Evaluation value, remove it and insert the new operator node instead.
				frontier_Q.remove(frontier_HT.get(operatorNodeStr));
				frontier_HT.remove(operatorNodeStr);
				frontier_HT.put(operatorNodeStr,newInfo);
				frontier_Q.add(newInfo);

			}
		}
	}
}

