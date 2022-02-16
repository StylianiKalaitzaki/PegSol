
public class TreeNode
{
	 int[][]p;
	 int count;
	 int h;
	 int g;
	 int f;
	 int direction; //the direction the node moved to for this node to be created
	 int i;			//row of peg that moved for this node to be created
	 int j;			//col of peg that moved for this node to be created
	 TreeNode parent;
	
	public TreeNode()
	{
		this.i=0;
		this.j=0;
		this.direction=0;
	}
	
	
	//counts and returns the number of pegs
	public int countAces()
	{
		int count=0;
		for(int i=0;i<p.length;i++)
		{
			for(int j=0;j<p[i].length;j++)
			{
				if(p[i][j]==1)
					count++;
			}
		}
		return count;
	}
	
	
	public void printTable()
	{
		for(int i=0;i<p.length;i++)
		{
			for(int j=0;j<p[i].length;j++)
			{
				System.out.print(this.p[i][j]);
			}
			System.out.println();
		}
	}
	
	//copies the table of a node to this table
	public void setTable(TreeNode node ,int rows,int cols)
	{
		this.p=new int[rows][cols];
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				this.p[i][j]=node.p[i][j];
	}
	
	
}
