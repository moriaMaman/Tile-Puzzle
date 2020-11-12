import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex1 {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub


		long startTime=System.currentTimeMillis();// initialize start time
		long endTime=System.currentTimeMillis();// initialize end time
		String algo="";
		boolean time=false;
		boolean open_list=false;
		ArrayList<String> file_lines=new ArrayList<String>();

		
		File file = new File("input.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		String st; 
	
		while ((st = br.readLine()) != null)//read from the input file
		{
			file_lines.add(st);//insert line from the file to array list.
		}
		
		algo=file_lines.get(0);//the algorithm type will be in the first line in the input file
		
		if(file_lines.get(1).contains("with")) {//with time\no time - will be in the second line.
			time=true;
		}
		if(file_lines.get(2).contains("with")) {//with open\no open - will be in the third line.
			open_list=true;
		}
		int mat_length=Integer.parseInt(file_lines.get(3).split("x")[0]);//Matrix length - will be in the fourth line.
		int mat_width=Integer.parseInt(file_lines.get(3).split("x")[1]);// Matrix width

		String [] black=file_lines.get(4).split("[Black: ,]+");//array of all black tiles
		String [] red=file_lines.get(5).split("[Red: ,]+");//array of all red tiles

		tileObj [] arr_puzzle=new tileObj [mat_length*mat_width]; // array that represent all the tiles. array[0] will represent the tile "1". array[1]- tile"2" and so on. 

		String goal="";
		//Initialize the puzzle's value represented by array
		for(int i=0;i<arr_puzzle.length-1;i++) {
			arr_puzzle[i]=new tileObj(String.valueOf(i+1),"G",1);//Initialize all the tiles color to green 
			goal=goal+","+String.valueOf(i+1); //make the goal node string
		}
		goal=goal+",_";//adding the "blank" tile to the end of the goal node string 
		goal=goal.substring(1);//to remove the "," in the start
		arr_puzzle[arr_puzzle.length-1]=new tileObj("_","G",1);//insert the "blank" tile representation to the tiles array
		
		for(int i=0;i<black.length;i++) {//change the color of all the black tiles to black 
			if (i!=0) {
				if(!black[i].equals("_")) {
				int index=Integer.parseInt(black[i]);
				arr_puzzle[index-1].setColor("B");
				}
				else {//in case this is the blank tile
					arr_puzzle[arr_puzzle.length-1].setColor("B");
				}
			}
		}
		for(int i=0;i<red.length;i++) {//change the color of all the black tiles to red and change their color price 
			if (i!=0) {
				if(!red[i].equals("_")) {
				int index=Integer.parseInt(red[i]);
				arr_puzzle[index-1].setColor("R");
				arr_puzzle[index-1].setPrice(30);
				}
				
				else {//in case this is the blank tile
					arr_puzzle[arr_puzzle.length-1].setColor("R");
					arr_puzzle[arr_puzzle.length-1].setPrice(30);
				}
			}
		}
		
		tileObj [][] start_puzzle= new tileObj [mat_length][mat_width];
		int index=6;// the first matrix line will be in the sixth line in the file
		for(int i=0;i<mat_length;i++) {// build the Matrix 
			String [] tiles=file_lines.get(index).split(",");
			index++;//to get to the next matrix line.
			for(int j=0;j<mat_width;j++) {
				if(!tiles[j].equals("_")) {
				start_puzzle[i][j]=arr_puzzle[Integer.parseInt(tiles[j])-1];
				}
				else {//if it is the blank tile
					start_puzzle[i][j]=arr_puzzle[arr_puzzle.length-1];
				}	
			}
		}
		
		
		
	    try {  //build the output file
	        File myObj = new File("output.txt"); 
	        myObj.createNewFile();
	      } catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();  
	      }
	    
	    FileWriter fileWriter = new FileWriter("output.txt");
	    PrintWriter printWriter = new PrintWriter(fileWriter);// for writing to the output file
	    
	    if(algo.equals("BFS")) {//if the requested algorithm is BFS
	    	startTime=System.currentTimeMillis();// start time 
			BFS bfs= new BFS();
			endTime=System.currentTimeMillis();// end time 
			nodeInfo BFSans=new nodeInfo();
			BFSans.swap(bfs.BFS(start_puzzle, goal,open_list));  
			if(BFSans.getPath().equals("no path")) {//If there is no path
				printWriter.println(BFSans.getPath());
				printWriter.println("Num: "+ bfs.numOfNodes);
			}
			else {//if the algorithm finds a path
				printWriter.println(BFSans.getPath().substring(1));
				printWriter.println("Num: "+ bfs.numOfNodes);
				printWriter.println("Cost: "+BFSans.getCost());
			}
		}
		else if(algo.equals("DFID")) {//if the requested algorithm is DFID
			startTime=System.currentTimeMillis();// start time 
			DFID dfid= new DFID();
			endTime=System.currentTimeMillis();// end time
			nodeInfo dfifAns=new nodeInfo();
			dfifAns.swap(dfid.DFID(start_puzzle, goal,open_list));  
			if(dfifAns.getPath().equals("no path")) {//If there is no path
				printWriter.println(dfifAns.getPath());
				printWriter.println("Num: "+ dfid.numOfNodes);
			}
			else {//if the algorithm finds a path
				printWriter.println(dfifAns.getPath().substring(1));
				printWriter.println("Num: "+ dfid.numOfNodes);
				printWriter.println("Cost: "+dfifAns.getCost());
			}
		
		}
		else if(algo.equals("A*")) {//if the requested algorithm is A*
			startTime=System.currentTimeMillis();// start time 
			Astar aStar= new Astar();
			endTime=System.currentTimeMillis();// end time
			nodeInfo astarAns=new nodeInfo();
			astarAns.swap(aStar.Astar(start_puzzle, goal,open_list));  
			if(astarAns.getPath().equals("no path")) {//If there is no path
				printWriter.println(astarAns.getPath());
				printWriter.println("Num: "+ aStar.numOfNodes);
			}
			else {//if the algorithm finds a path
				printWriter.println(astarAns.getPath().substring(1));
				printWriter.println("Num: "+ aStar.numOfNodes);
				printWriter.println("Cost: "+astarAns.getCost());
			}
			
		}
		else if(algo.equals("IDA*")) {//if the requested algorithm is IDA*
			startTime=System.currentTimeMillis();// start time 
			IDAstar idaStar= new IDAstar();
			endTime=System.currentTimeMillis();// end time
			nodeInfo IDAans=new nodeInfo();
			IDAans.swap(idaStar.IDAstar(start_puzzle, goal,open_list));  
			if(IDAans.getPath().equals("no path")) {//If there is no path
				printWriter.println(IDAans.getPath());
				printWriter.println("Num: "+ idaStar.numOfNodes);
			}
			else {//if the algorithm finds a path
				printWriter.println(IDAans.getPath().substring(1));
				printWriter.println("Num: "+ idaStar.numOfNodes);
				printWriter.println("Cost: "+IDAans.getCost());	
			}
			
		}
		else if(algo.equals("DFBnB")) {//if the requested algorithm is DFBnB
			startTime=System.currentTimeMillis();//start time
			DFBnB dfbnb= new DFBnB();
			endTime=System.currentTimeMillis();//end time
			nodeInfo DFBnBans=new nodeInfo();
			DFBnBans.swap(dfbnb.DFBnB(start_puzzle, goal,open_list));  
			if(DFBnBans.getPath().equals("no path")) {//If there is no path
				printWriter.println(DFBnBans.getPath());
				printWriter.println("Num: "+ dfbnb.numOfNodes);
				
			}
			else {//if the algorithm finds a path
				printWriter.println(DFBnBans.getPath().substring(1));
				printWriter.println("Num: "+ dfbnb.numOfNodes);
				printWriter.println("Cost: "+DFBnBans.getCost());
			}
		}
	    
	    if(time) {//if required to display the algorithm run time
	    	float realTime=(endTime-startTime)/1000F;
	    	fileWriter.write(realTime +" seconds");
	    }
	    fileWriter.close();	//close the output file

	}

}
