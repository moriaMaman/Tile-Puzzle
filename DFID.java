import java.util.Hashtable;
import java.util.Set;
/**
 * This class represents a DFID algorithm on a tile puzzle . 
 *
 * @author Moria Maman 
 */

public class DFID extends algoHelpFunctions {
	int numOfNodes=1;
	int total=0;//total cost
	Hashtable< String,nodeInfo> loopAvoidance;
	boolean with_open=false;
	int rounds=1;

	//**************************************************
	// Public method
	// **************************************************
	public nodeInfo DFID (tileObj [][] startPuzzle, String goal,boolean withOpen) {
		if(fromMatrixToString(startPuzzle).equals(goal)) {//check if the start node is already the goal node
			return new nodeInfo(0,"no path",0,startPuzzle); 
		}
		if(firstCheck(startPuzzle)) {//if one of the black tiles is not in its right position, it means it can't be moved and that there is no solution 
			return new nodeInfo(0,"no path");
		}
		int limit=1;
		while(true) {
			this.with_open=withOpen;
			this.loopAvoidance = new Hashtable<String,nodeInfo>();
			nodeInfo startNode=new nodeInfo();
			startNode.copyNode(startPuzzle);
			nodeInfo result=new nodeInfo();
			result=Limited_DFS(startNode,goal,limit);
			if (!result.getPath().contains("cutoff")) {
				return result;
			}
			limit++;
		}
	}
	//**************************************************
	// Private method
	// **************************************************
	private nodeInfo Limited_DFS(nodeInfo nodeInf, String goal, int limit) {
		tileObj [][] node=nodeInf.getNode();
		String nodeStr=fromMatrixToString(node);
		nodeInfo result=new nodeInfo();
		if(nodeStr.equals(goal)) {
			return nodeInf;
		}
		if (limit==0) {
			result.swap(new nodeInfo(0,"cutoff"));
			return result;
		}

		loopAvoidance.put(nodeStr,nodeInf);
		boolean isCutoff=false;	

		int[] blankIndex=new int[2];//will represents the blank location
		blankIndex=blank_index(node);//find the blank index
		int blankLengthIndex=blankIndex[0];//length index of the "_" 
		int blankWidthIndex=blankIndex[1];//width index of the "_" 
		if(node[blankLengthIndex][blankWidthIndex].color.equals("B"))  return new nodeInfo(0,"no path");//if the blank can not be moved
		if (blankLengthIndex==-1) return new nodeInfo(0,"no path");//if there is no blank in the puzzle

		if(blankWidthIndex!=((node[0].length)-1)) {//if left node is possible
			String operation=node[blankLengthIndex][blankWidthIndex+1].value+"R";//the opposite operation
			if(!(node[blankLengthIndex][blankWidthIndex+1].color.equals("B")) && !operation.equals(nodeInf.getFather())) {//check if the node is not black, and can be moved
				nodeInfo operatorInfo=help(node,blankLengthIndex,blankWidthIndex,"L",goal);
				result=Limited_DFS(operatorInfo,goal,limit-1);
				if(result.getPath().equals("cutoff")) {
					isCutoff=true;
				}
				else if(!result.getPath().equals("fail")) {
					return result;
				}
			}
		}
		if(blankLengthIndex!=node.length-1) {//if up node is possible
			String operation=node[blankLengthIndex+1][blankWidthIndex].value+"D";//the opposite operation
			if(!node[blankLengthIndex+1][blankWidthIndex].color.equals("B") && !operation.equals(nodeInf.getFather())) {//check if the node is not black, and can be moved
				nodeInfo operatorInfo=help(node,blankLengthIndex,blankWidthIndex,"U",goal);
				result=Limited_DFS(operatorInfo,goal,limit-1);
				if(result.getPath().equals("cutoff")) {
					isCutoff=true;
				}
				else if(!result.getPath().equals("fail")) {
					return result;
				}
			}
		}
		if(blankWidthIndex!=0) {//if right node is possible 
			String operation=node[blankLengthIndex][blankWidthIndex-1].value+"L";//the opposite operation
			if(!(node[blankLengthIndex][blankWidthIndex-1].color.equals("B")) && !operation.equals(nodeInf.getFather())) {//check if the node is not black, and can be moved
				nodeInfo operatorInfo=help(node,blankLengthIndex,blankWidthIndex,"R",goal);
				result=Limited_DFS(operatorInfo,goal,limit-1);
				if(result.getPath().equals("cutoff")) {
					isCutoff=true;
				}
				else if(!result.getPath().equals("fail")) {
					return result;
				}
			}
		}
		if(blankLengthIndex!=0) {//if down node is possible 
			String operation=node[blankLengthIndex-1][blankWidthIndex].value+"U";//the opposite operation
			if(!(node[blankLengthIndex-1][blankWidthIndex].color.equals("B")) && !operation.equals(nodeInf.getFather())) {//check if the node is not black, and can be moved
				nodeInfo operatorInfo=help(node,blankLengthIndex,blankWidthIndex,"D",goal);
				result=Limited_DFS(operatorInfo,goal,limit-1);
				if(result.getPath().equals("cutoff")) {
					isCutoff=true;
				}
				else if(!result.getPath().equals("fail")) {
					return result;
				}
			}
		}


		if(this.with_open) {// for the open list print 
			System.out.println("round: "+rounds);//print the number of rounds when print the open list.
			rounds++;
			Set <String> keys= loopAvoidance.keySet();
			for(String key: keys){
				printNode(loopAvoidance.get(key).getNode());
				System.out.println("*******");
			}
		}	

		loopAvoidance.remove(nodeStr);
		if(isCutoff==true) {
			result.swap(new nodeInfo(0,"cutoff"));
			return result; 
		}
		return new nodeInfo(0,"fail");
	}

	/**
	    * this function crate a new operator child node, with a given string direction value as input("L" for left node child, "U" for up and so on).
	    * this function check if node is not in the loop avoidance hash table, and helps update its information. 
	    */
	private nodeInfo  help(tileObj [][] node,int blankLengthIndex,int blankWidthIndex, String direction,String goal) {
		tileObj [][] operator=childMat(blankLengthIndex,blankWidthIndex,node,direction);//crate the operator child node
		String operatorStr= fromMatrixToString(operator);
		nodeInfo newInfo=new nodeInfo();
		this.numOfNodes++;
		if( !loopAvoidance.containsKey(operatorStr)) {//check if the matrix node is not in the path nodes list 
			newInfo.setFather(operator[blankLengthIndex][blankWidthIndex].value+direction);//update the node operation ("8D" as example)
			newInfo.swap(loopAvoidance.get(fromMatrixToString(node)));//get the path and cost from the father node 
			newInfo.copyNode(operator);
			String newPath="-"+operator[blankLengthIndex][blankWidthIndex].value+direction;//make new string of the new operator child operation
			newInfo.addPath(newPath);//add the new operation to the child path
			newInfo.addCost(operator[blankLengthIndex][blankWidthIndex].colorPrice);//add the cost of the movement of the new operation
		}
		return newInfo;
	}
}
