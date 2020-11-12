import java.util.Hashtable;
import java.util.Stack;

/**
 * This class represents a IDA* algorithm on a tile puzzle . 
 *
 * @author Moria Maman 
 */
public class IDAstar extends algoHelpFunctions{
	int numOfNodes;
	int total;//total cost
	Hashtable< String,nodeInfo> loopAvoidance_HT;
	Stack<nodeInfo> loopAvoidance_stack;
	int minEvaluationValue;
	int threshold;

	// **************************************************
    // Constructor
    // ************************************************** 
	IDAstar(){
		this.numOfNodes=1;
		this.total=0;
		this.loopAvoidance_HT = new Hashtable<String,nodeInfo>();
		this.loopAvoidance_stack=new Stack<nodeInfo>();
	}

	// **************************************************
    // Public methods
    // **************************************************
	public nodeInfo IDAstar(tileObj [][] startPuzzle, String goal,boolean withOpen) {
		if(fromMatrixToString(startPuzzle).equals(goal)) {//check if the start node is already the goal node
			return new nodeInfo(0,"no path",0,startPuzzle); 
		}
		if(firstCheck(startPuzzle)) {//if one of the black tiles is not in its right position, it means it can't be moved and that there is no solution 
			return new nodeInfo(0,"no path");
		}
		threshold=heuristicFunction(startPuzzle);
		nodeInfo nodeInf= new nodeInfo(0,"",threshold,startPuzzle);//start mode info
		int rounds=1;//for the open list print mode
		while(threshold<Integer.MAX_VALUE) {
			nodeInf.setOut();
			minEvaluationValue=Integer.MAX_VALUE;
			String nodeStr=fromMatrixToString(startPuzzle);
			loopAvoidance_stack.push(nodeInf);
			loopAvoidance_HT.put(nodeStr,nodeInf);
			while(loopAvoidance_stack.size()>0) {
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
				tileObj [][] nodeMatrix=node.getNode();
				if(node.isOut()) {//if the node is marked as out
					loopAvoidance_HT.remove(fromMatrixToString(nodeMatrix));
				}
				else {
					node.markAsOut();
					loopAvoidance_stack.push(node);
					int[] blankIndex=new int[2];//will represents the blank location
					blankIndex=blank_index(nodeMatrix);//find the blank index
					int blankLengthIndex=blankIndex[0];//length index of the "_" 
					int blankWidthIndex=blankIndex[1];//width index of the "_" 
					if(nodeMatrix[blankLengthIndex][blankWidthIndex].color.equals("B"))  return new nodeInfo(0,"no path");//if the blank can not be moved
					if (blankLengthIndex==-1) return new nodeInfo(0,"no path");//if there is no blank in the puzzle


					if(blankWidthIndex!=((nodeMatrix[0].length)-1)) {//if left node is possible
						String opposit_operation=nodeMatrix[blankLengthIndex][blankWidthIndex+1].value+"R";//the opposite operation
						if(!(nodeMatrix[blankLengthIndex][blankWidthIndex+1].color.equals("B")) && !opposit_operation.equals(node.getFather())) {//check if the node is not black, and can be moved
							nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"L",goal);
							if(!result.getPath().equals("")) {
								return result;
							}
						}
					}
					if(blankLengthIndex!=nodeMatrix.length-1) {//if up node is possible 
						String opposit_operation=nodeMatrix[blankLengthIndex+1][blankWidthIndex].value+"D";//the opposite operation
						if(!(nodeMatrix[blankLengthIndex+1][blankWidthIndex].color.equals("B")) && !opposit_operation.equals(node.getFather())) {//check if the node is not black, and can be moved
							nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"U",goal);
							if(!result.getPath().equals("")) {
								System.out.println(result.getPath());
								return result;
							}
						}
					}

					if(blankWidthIndex!=0) {//if right node is possible 
						String opposit_operation=nodeMatrix[blankLengthIndex][blankWidthIndex-1].value+"L";//the opposite operation
						if(!(nodeMatrix[blankLengthIndex][blankWidthIndex-1].color.equals("B")) && !opposit_operation.equals(node.getFather())) {
							nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"R",goal);
							if(!result.getPath().equals("")) {
								return result;
							}
						}
					}
					if(blankLengthIndex!=0) {//if down node is possible 
						String opposit_operation=nodeMatrix[blankLengthIndex-1][blankWidthIndex].value+"U";//the opposite operation
						if(!(nodeMatrix[blankLengthIndex-1][blankWidthIndex].color.equals("B")) && !opposit_operation.equals(node.getFather())) {
							nodeInfo result=help(node,blankLengthIndex,blankWidthIndex,"D",goal);
							if(!result.getPath().equals("")) {
								return result;
							}
						}
					}
				}
			}
			this.threshold=this.minEvaluationValue;
		}
		return new nodeInfo(0,"no path");
	}


	//**************************************************
	// Private method
	// **************************************************
	/**
	    * this function crate a new operator child node, with a given string direction value as input("L" for left node child, "U" for up and so on). 
	    * the function check if the new node is not in the loopAvoidance hash table, and helps update its information. 
	    * 
	    */
	private nodeInfo help(nodeInfo node,int blankLengthIndex,int blankWidthIndex,String direction,String goal) {
		tileObj [][] nodeMatrix=node.getNode();
		tileObj [][] operator=childMat(blankLengthIndex,blankWidthIndex,nodeMatrix,direction);//crate the operator child node
		this.numOfNodes++;
		String nodeStr= fromMatrixToString(operator);
		nodeInfo operatorInfo=new nodeInfo();
		String Operation=operator[blankLengthIndex][blankWidthIndex].value+direction;//the node operation ("8D" as example)
		operatorInfo.setFather(Operation);//update the node operation
		operatorInfo.swap(node);//get the path and cost from the father node 
		operatorInfo.copyNode(operator);//insert the operator matrix to the object 
		operatorInfo.addPath("-"+Operation);//update the operator node path
		operatorInfo.addCost(operator[blankLengthIndex][blankWidthIndex].colorPrice);//update the operator node cost  
		operatorInfo.setHeuristicFunResulte(heuristicFunction(operator));//calculate and update the heuristic Function of the node matrix 
		if(operatorInfo.getEvaluation()>threshold) {
			minEvaluationValue=Math.min(operatorInfo.getEvaluation(), minEvaluationValue);
			return new nodeInfo();
		}
		if(loopAvoidance_HT.contains(nodeStr)) {
			nodeInfo sameNodeStrInfo=loopAvoidance_HT.get(nodeStr);
			if(sameNodeStrInfo.isOut()==false) {
				if(sameNodeStrInfo.getEvaluation()>operatorInfo.getEvaluation()) {// if the exists node in the open list has a bigger Evaluation value, remove it and insert the new operator node instead.
					loopAvoidance_HT.remove(nodeStr);
					loopAvoidance_stack.remove(sameNodeStrInfo);
				}		
				else {
					return new nodeInfo();
				}
			}
			else {
				return new nodeInfo();
			}
		}
		if(nodeStr.equals(goal)) {
			return operatorInfo;
		}
		else {
			loopAvoidance_HT.put(nodeStr,operatorInfo);
			loopAvoidance_stack.push(operatorInfo);

		}

		return new nodeInfo();
	}
}
