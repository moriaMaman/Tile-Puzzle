import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
/**
 * This class represents a BFS algorithm on a tile puzzle . 
 *
 * @author Moria Maman 
 */

public class BFS extends algoHelpFunctions{
	int numOfNodes;
	int total;//total cost
	Hashtable< String,nodeInfo> frontier_HT; 
	Hashtable<String,nodeInfo> exploreSet; 
	Queue<tileObj[][]> frontier_Q;
	
	// **************************************************
    // Constructor
    // ************************************************** 
	BFS(){
		numOfNodes=1;
		 total=0;
		 frontier_HT = new Hashtable<String,nodeInfo>();
		 exploreSet = new Hashtable<String,nodeInfo>();
		 frontier_Q = new LinkedList<>(); 
	}
	
	// **************************************************
    // Public methods
    // **************************************************
	
	public nodeInfo BFS (tileObj [][] startPuzzle, String goal,Boolean withOpen) {
		if(fromMatrixToString(startPuzzle).equals(goal)) {//check if the start node is already the goal node 
			return new nodeInfo(0,"no path",0,startPuzzle); 
		}
		if(firstCheck(startPuzzle)) {//if one of the black tiles is not in its right position, it means it can't be moved and that there is no solution 
			return new nodeInfo(0,"no path");
		}
		frontier_Q.add(startPuzzle);
		nodeInfo info=new nodeInfo(0,"");
		String startNodeStr=fromMatrixToString(startPuzzle);
		frontier_HT.put(startNodeStr,info);
		int rounds=1;//for the open list print mode
	
		while(frontier_HT.size()>0) {
			
			if(withOpen) {// for the open list print 
				System.out.println("round: "+rounds);//print the number of rounds when print the open list.
				rounds++;
				for(tileObj[][] nodeMat : frontier_Q)
			    {
					printNode(nodeMat);
					System.out.println("*******");
				}
			}
			tileObj [][] node= frontier_Q.remove();
			String nodeStr=fromMatrixToString(node);
			nodeInfo node_info= new nodeInfo();
			node_info=frontier_HT.get(nodeStr);
			exploreSet.put(nodeStr,node_info);//insert the node to the explore set
			frontier_HT.remove(nodeStr);//remove node from the frontier

			int[] blankIndex=new int[2];//will represents the blank location
			blankIndex=blank_index(node);
			int blankLengthIndex=blankIndex[0];//length index of the "_" 
			int blankWidthIndex=blankIndex[1];//width index of the "_" 
			if(node[blankLengthIndex][blankWidthIndex].color.equals("B")) return new nodeInfo(0,"no path");// if the blank isn't in the right place and black it means he can't be moved.
			if (blankLengthIndex==-1) return new nodeInfo(0,"no path");//if there is no blank in the node.

			if(blankWidthIndex!=((node[0].length)-1)) {//if left node is possible
				String operation=node[blankLengthIndex][blankWidthIndex+1].value+"R";//the opposite operation
				if(!(node[blankLengthIndex][blankWidthIndex+1].color.equals("B")) && !(operation.equals(node_info.getFather()))) {//check if the node is not black which means it can be moved,and if it is not the opposite operation of the father. 
				
					nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"L",goal);
					if (!result.getPath().equals("")) {
						return result;
					}
				}
			}
			if(blankLengthIndex!=node.length-1) {//if up node is possible
				String operation=node[blankLengthIndex+1][blankWidthIndex].value+"D";//the opposite operation
				if(!(node[blankLengthIndex+1][blankWidthIndex].color.equals("B")) && !operation.equals(node_info.getFather())) {//check if the node is not black which means it can be moved,and if it is not the opposite operation of the father.
					nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"U",goal);
					if (!result.getPath().equals("")) {
						return result;
					}
				}
			}
			if(blankWidthIndex!=0) {//if right node is possible
				String operation=node[blankLengthIndex][blankWidthIndex-1].value+"L";//the opposite operation
				if(!(node[blankLengthIndex][blankWidthIndex-1].color.equals("B")) && !operation.equals(node_info.getFather())) {//check if the node is not black which means it can be moved,and if it is not the opposite operation of the father.
					nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"R",goal);
					if (!result.getPath().equals("")) {
						return result;
					}
				}
			}
			if(blankLengthIndex!=0) {//if down node is possible
				String operation=node[blankLengthIndex-1][blankWidthIndex].value+"U";//the opposite operation
				if(!(node[blankLengthIndex-1][blankWidthIndex].color.equals("B")) && !operation.equals(node_info.getFather())) {//check if the node is not black which means it can be moved,and if it is not the opposite operation of the father.
					nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"D",goal);
					if (!result.getPath().equals("")) {
						return result;
					}
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
	private nodeInfo help(tileObj [][] node,int blankLengthIndex, int blankWidthIndex, String direction,String goal ) {
		tileObj [][] operator= childMat(blankLengthIndex,blankWidthIndex,node,direction);//crate the operator child node
		String operatorStr= fromMatrixToString(operator);
		this.numOfNodes++;
		if(!frontier_HT.containsKey(operatorStr) && !exploreSet.containsKey(operatorStr)) {//check if the matrix node is not in the open list and not in the close list 	
			nodeInfo newInfo=new nodeInfo();
			newInfo.setFather(operator[blankLengthIndex][blankWidthIndex].value+direction);//update the node operation ("8D" as example)
			newInfo.swap(exploreSet.get(fromMatrixToString(node)));//get the path and cost from the father node 
			String newPath="-"+operator[blankLengthIndex][blankWidthIndex].value+direction;//make new string of the new operator child operation
			newInfo.addPath(newPath);//add the new operation to the child path
			newInfo.addCost(operator[blankLengthIndex][blankWidthIndex].colorPrice);//add the cost of the movement of the new operation
			if(operatorStr.equals(goal)) {
				this.total=newInfo.getCost();
				return newInfo;
			}
			frontier_HT.put(operatorStr,newInfo);
			frontier_Q.add(operator);
			
		}
		return new nodeInfo();
		
	}

}
