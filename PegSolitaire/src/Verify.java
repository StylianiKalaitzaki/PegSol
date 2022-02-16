import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*This program verifies whether a solution is valid*/

public class Verify
{
	public static void main(String[] args) 
	{
		if (args.length != 2) 
		{
	   		System.out.println("Usage: java verify <input filename> <solution filename>");
	   		System.exit(1);
		}
		
   	
		Main.readFile(args[0]);
		if(checkSolution(args[1],Main.pegSol))
			System.out.println("Solution OK!");
		else
			System.out.println("Solution not valid.");
	}
	
	//this function returns true if the steps in the solution file can lead the initial 
	//table to a solution
	public static boolean checkSolution(String file,int[][] initial)
	{
		int[][] moves=null;
		int steps=0;
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			steps=Integer.parseInt(line);
			moves=new int[steps][4];
			
			for(int i=0;i<steps;i++)
			{
				line = reader.readLine();
				String[] temp=line.split(" ");
				if(temp.length!=4)
				{
					System.out.println("Wrong output");
					reader.close();
					return false;
				}

				for(int j=0;j<4;j++)
					moves[i][j]=Integer.parseInt(temp[j]);
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
		
		for(int i=0;i<steps;i++)
		{
				initial[moves[i][0]-1][moves[i][1]-1]=2;
				initial[((moves[i][0]+moves[i][2])/2)-1][((moves[i][1]+moves[i][3])/2)-1]=2;
				initial[moves[i][2]-1][moves[i][3]-1]=1;
		}
		
		if(isSolution(initial))
			return true;
		return false;
	}
	
	public static boolean isSolution(int[][] p)
	{
		int count=0;
		for(int i=0;i<p.length;i++)
		{
			for(int j=0;j<p[i].length;j++)
			{
				if (p[i][j]==1)
					count++;
			}
		}
		if(count>1)
			return false;
		return true;
	}
}
