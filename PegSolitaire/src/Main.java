import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main 
{
	static int rows, cols;
	static int[][] pegSol=null;
	static final int breadth=1;
	static final int depth=2;
	static final int best=3;
	static final int astar=4;
	static FrontierNode frontierHead=null;
	static FrontierNode frontierTail=null;
	static int[] x= {0,1,0,-1};	//0: same row 1: next row -1: previous row
	static int[] y= {-1,0,1,0}; //0: same col 1: next col -1: previous col
	static int[][] pegSolFinal;
	static final int TIMEOUT=300; // Program terminates after TIMEOUT secs
	static long start;
	static long end;
	
	public static void main(String[] args) 
	{
		  int method;
		  String infile,outfile;
		  if (args.length != 3) 
			{
		   		System.out.println("Usage: pegsol.exe <method> <input filename> <output filename>");
		   		System.exit(1);
			}
		
		method = get_method(args[0]);
		infile= args[1];
		outfile=args[2];
			
				
		readFile(infile);
		
		TreeNode solution_node=new TreeNode();
		
		start=System.currentTimeMillis();
		
		initializeSearch(pegSol, method);

		solution_node=search(method);
		
		end=System.currentTimeMillis();
		if (solution_node!=null)
			{
				extract_solution(solution_node);
				if (solution_node.g>0)
				{
					System.out.printf("Solution found! (%d moves)\n",solution_node.g);
					System.out.println("Time spent: "+(end-start)+" ms");
					writeFile(pegSolFinal,outfile);
				}
			}
		else
			System.out.printf("No solution found.\n");

		
		
	}
	
	public static void readFile (String s)
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(s));
			String line = reader.readLine();
			String[] temp=line.split(" ");
			rows=Integer.parseInt(temp[0]);
			cols=Integer.parseInt(temp[1]);
			pegSol=new int[rows][cols];
			
			for(int i=0;i<rows;i++)
			{
				line = reader.readLine();
				if(line == null)
					break;
				
				String[] nums=line.split(" ");
				for(int j=0;j<cols;j++)
				{
					pegSol[i][j]=Integer.parseInt(nums[j]);
				}
			}
			reader.close();
		} 
		catch (FileNotFoundException exc) 
		{
			exc.printStackTrace();
		}
		catch(IOException exc) 
		{
			exc.printStackTrace();
		}
		
		
	}
	
	public static void print(int[][] a)
	{
		for(int i=0;i<a.length;i++)
		{
			for(int j=0;j<a[i].length;j++)
				System.out.print(a[i][j]+" ");
			System.out.println();
		}
	}
	
	public static int get_method(String s)
	{
		if (s.equalsIgnoreCase("breadth"))
			return  breadth;
		else if (s.equalsIgnoreCase("depth"))
			return depth;
		else if (s.equalsIgnoreCase("best"))
			return best;
		else if (s.equalsIgnoreCase("astar"))
			return astar;
		else
			return -1;
	}

	static int addFrontierFront(TreeNode node)
	{
		// Creating the new frontier node
		FrontierNode newFrontNode=new FrontierNode();
		

		newFrontNode.n=node;
		newFrontNode.previous=null;
		newFrontNode.next=frontierHead;

		if (frontierHead==null)
		{
			frontierHead=newFrontNode;
			frontierTail=newFrontNode;
		}
		else
		{
			frontierHead.previous=newFrontNode;
			frontierHead=newFrontNode;
		}
		return 0;
	}

	
	static int addFrontierInOrder(TreeNode node)
	{
		// Creating the new frontier node
		FrontierNode newFrontNode=new FrontierNode();
		

		newFrontNode.n=node;
		newFrontNode.previous=null;
		newFrontNode.next=null;

		if (frontierHead==null)
		{
			frontierHead=newFrontNode;
			frontierTail=newFrontNode;
		}
		else
		{
			FrontierNode pt;
			pt=frontierHead;

			// Search in the frontier for the first node that corresponds to either a larger f value
			// or to an equal f value but larger h value
			// Note that for the best first search algorithm, f and h values coincide.
			while (pt!=null && (pt.n.f<node.f || (pt.n.f==node.f && pt.n.h<node.h)))
				pt=pt.next;

			if (pt!=null)
			{
				// new_frontier_node is inserted before pt .
				if (pt.previous!=null)
				{
					pt.previous.next=newFrontNode;
					newFrontNode.next=pt;
					newFrontNode.previous=pt.previous;
					pt.previous=newFrontNode;
				}
				else
				{
					// In this case, new_frontier_node becomes the first node of the frontier.
					newFrontNode.next=pt;
					pt.previous=newFrontNode;
					frontierHead=newFrontNode;
				}
			}
			else
			{
				// if pt==NULL, new_frontier_node is inserted at the back of the frontier
				frontierTail.next=newFrontNode;
				newFrontNode.previous=frontierTail;
				frontierTail=newFrontNode;
			}
		}

		return 0;
	}
	
	static int addFrontierBack(TreeNode node)
	{
		// Creating the new frontier node
		FrontierNode new_frontier_node=new FrontierNode();
		
		new_frontier_node.n=node;
		new_frontier_node.next=null;
		new_frontier_node.previous=frontierTail;

		if (frontierTail==null)
		{
			frontierHead=new_frontier_node;
			frontierTail=new_frontier_node;
		}
		else
		{
			frontierTail.next=new_frontier_node;
			frontierTail=new_frontier_node;
		}

		return 0;
	}

	
	

	static void initializeSearch(int[][] pegSol, int method)
	{
		TreeNode root=new TreeNode();	// the root of the search tree.

		// Initialize search tree
		root.parent=null;
		root.direction=-1;
		root.p=pegSol;
		root.g=0;
		root.h=heuristic2(root.p)-root.g;
		//root.h=heuristic(root.p);
		if (method==best)
			root.f=root.h;
		else if (method==astar)
			root.f=root.g+root.h;
		else
			root.f=0;

		// Initialize frontier
		addFrontierFront(root);
	}

	//This function computes the manhattan distance of a peg from all the other pegs.
	//Returns the sum of the distances for the peg in (i,j) position
	public static int manhattan_distance(int i, int j, int[][] p)
	{

		int sum=0;
		for(int x=0;x>rows;x++)
		{
			for(int y=0;y<cols;y++)
			{
				if(p[x][y]==1)
					sum+= Math.abs(i-x)+Math.abs(j-y);
			}
		}
		return sum;
	}

	//This function adds the sum of the manhattan distances of each peg and
	//returns the score
	public static int heuristic(int[][] p)
	{
		int i,j;
		int score=0;
		for (i=0;i<rows;i++)
		{
			for (j=0;j<cols;j++)
			{
				if(p[i][j]==1)
					score+=manhattan_distance(i,j,p);
			}
		}
		return score;
	}
	//This function calculates the rectangle area the pegs cover
	public static int heuristic2(int[][] p)
	{
		int score=0;
		
		score=height(p)*breadth(p);
		
		return score;
	}
	
	//This function calculates the height of the area the pegs cover
	public static int height(int[][] p)
	{
		int mini=rows,maxi=0;
		for (int i=0;i<rows;i++)
		{
			for (int j=0;j<cols;j++)
			{
				if(p[i][j]==1 && i<mini)
					mini=i;
				if( p[i][j]==1 && i>maxi)
					maxi=i;
			}
		}
		
		return (Math.abs(maxi-mini)+1);
	}
	//This function calculates the breadth of the area the pegs cover
	public static int breadth(int[][] p)
	{
		int minj=cols,maxj=0;
		for (int i=0;i<rows;i++)
		{
			for (int j=0;j<cols;j++)
			{
				if(p[i][j]==1 && j<minj)
					minj=j;
				if( p[i][j]==1 && j>maxj)
					maxj=j;
			}
		}
		return (Math.abs(maxj-minj)+1);
	}
	
	public static TreeNode search(int method)
	{
		long t;
		int err = 0;
		FrontierNode temp_frontier_node=new FrontierNode();
		TreeNode currentNode= new TreeNode();


		while (frontierHead!=null)
		{
			

			t=System.currentTimeMillis();
			if (t-start>1000*TIMEOUT)
			{
				System.out.printf("Timeout\n");
				return null;
			}
			// Extract the first node from the frontier
			currentNode=frontierHead.n;
			
			
			if (isSolution(currentNode))
				return currentNode;
			
		
			// Delete the first node of the frontier
			temp_frontier_node=frontierHead;
			frontierHead=frontierHead.next;
			temp_frontier_node=null;
			if (frontierHead==null)
				frontierTail=null;
			else
				frontierHead.previous=null;

			// Find the children of the extracted node
			
			err=findChildren(currentNode,method);
			if (err<0)
	        {
	            System.out.printf("Memory exhausted while creating new frontier node. Search is terminated...\n");
				return null;
	        }
		}
		return null;
	}
	
	public static boolean isSolution(TreeNode node)
	{
		if (node.countAces()==1)
				return true;
		return false;
	}
	

	//This function extracts the position of the pegs that must move
	//and saves them along with their destined positions in a global table
	public static void extract_solution (TreeNode solution_node)
	{

		TreeNode temp_node=solution_node;
		int solution_length=solution_node.g;

		temp_node=solution_node;
		pegSolFinal=new int[solution_length][4];
		int k=solution_length-1;
		while (temp_node.parent!=null)
		{			
				pegSolFinal[k][0]=temp_node.i+1;
				pegSolFinal[k][1]=temp_node.j+1;
				pegSolFinal[k][2]=temp_node.i+1+2*x[temp_node.direction];
				pegSolFinal[k][3]=temp_node.j+1+2*y[temp_node.direction];
			
				k--;
				temp_node=temp_node.parent;
		}
	}
	
	//This function writes the solution into a file
	public static void writeFile(int[][] solution ,String file)
	{
		try
		{
			File f = new File(file);
			FileWriter writer = new FileWriter(f);
			
			writer.write(solution.length+"\n");
			for(int i=0;i<solution.length;i++) 
			{
				for(int j=0;j<solution[i].length;j++)
				{
					writer.write(solution[i][j]+" ");
				}
			  writer.write(System.lineSeparator());
			}
			
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public static int findChildren(TreeNode node,int method)
	{
		
		
		TreeNode child=null;
		int k=node.count;//this is the number of children a node has found up to this moment
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				if (node.p[i][j]==1)//if peg exists
				{
					for(int d=0;d<4 ;d++)
					{
						
						if(legalMove(node.p,i,j,d) && k==0)//rejects a legal move if k>0 because it has already checked the k first children
						{
							child=new TreeNode();
							child.setTable(node, rows, cols);
							child.parent=node;
							child.g=node.g+1;
							child.p[i][j]=2;
							child.p[i+x[d]][j+y[d]]=2;
							child.p[i+x[d]+x[d]][j+y[d]+y[d]]=1;
							child.direction=d;
							child.i=i;
							child.j=j;
							node.count++;
							
							// Computing the heuristic value 
							child.h=heuristic2(child.p)-child.g;//this heuristic computes the area of the pegs
							
							/*the heuristic below computes  the manhattan distances*/
							//child.h=heuristic(child.p);
							
							if (method==best)
								child.f=child.h;
							else if (method==astar)
								child.f=child.g+child.h;
							else
								child.f=0;
							
							int err=0;
					        if (method==depth)
					        	err=addFrontierFront(child);
					        else if (method==breadth)
								err=addFrontierBack(child);
							else if (method==best || method==astar)
								err=addFrontierInOrder(child);
							if (err<0)
								return -1;
							
						}
						if(k>0)
							k--;
					}
				}
			}
		}
		return 1;
	}
	
	//This function checks whether a peg in (i,j) position can move towards d direction
	//Returns true if move is possible and false if not
	public static boolean legalMove(int[][] p,int i,int j,int d)
	{
		if(i+2*x[d]>=0 && i+2*x[d]<rows && j+2*y[d]>=0 && j+2*y[d]<cols && p[i+x[d]][j+y[d]]==1  && p[i+2*x[d]][j+2*y[d]]==2 )
			return true;
		return false;
		
	}
	
	 
}

