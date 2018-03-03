import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

class Test
{
   static boolean visite[];
   public static void dfs(Graph g, int u)
	 {
		visite[u] = true;
		System.out.println("Je visite " + u);
		for (Edge e: g.next(u))
		  if (!visite[e.to])
			dfs(g,e.to);
	 }

   public static void testHeap()
	 {
		// Crée ue file de priorité contenant les entiers de 0 à 9, tous avec priorité +infty
		Heap h = new Heap(10);
		h.decreaseKey(3,1664);
		h.decreaseKey(4,5);
		h.decreaseKey(3,8);
		h.decreaseKey(2,3);
		// A ce moment, la priorité des différents éléments est:
		// 2 -> 3
		// 3 -> 8
		// 4 -> 5
		// tout le reste -> +infini
		int x=  h.pop();
		System.out.println("On a enlevé "+x+" de la file, dont la priorité était " + h.priority(x));
		x=  h.pop();
		System.out.println("On a enlevé "+x+" de la file, dont la priorité était " + h.priority(x));
		x=  h.pop();
		System.out.println("On a enlevé "+x+" de la file, dont la priorité était " + h.priority(x));
		// La file contient maintenant uniquement les éléments 0,1,5,6,7,8,9 avec priorité +infini
	 }
   
   public static void testGraph()
	 {
		int n = 5;
		int i,j;
		Graph g = new Graph(n*n+2);
		
		for (i = 0; i < n-1; i++)
		  for (j = 0; j < n ; j++)
			g.addEdge(new Edge(n*i+j, n*(i+1)+j, 1664 - (i+j)));

		for (j = 0; j < n ; j++)		  
		  g.addEdge(new Edge(n*(n-1)+j, n*n, 666));
		
		for (j = 0; j < n ; j++)					
		  g.addEdge(new Edge(n*n+1, j, 0));
		
		g.addEdge(new Edge(13,17,1337));
		g.writeFile("test.dot");
		// dfs à partir du sommet 3
		visite = new boolean[n*n+2];
		dfs(g, 3);
	 }
   
   public static void main(String[] args)
	 {
	 	String filename ="";

	 	/* File selection */
	 	if (args.length == 1){
	 		filename = args[0];
			if(!filename.endsWith(".pgm"))
				filename = filename + ".pgm";
	 		System.out.println("Steam Carving:");
	 		System.out.println("\tSelected file: "+"\u001B[36m"+"filename"+"\u001B[0m");
		} else {
			System.out.println("Steam Carving:");
			System.out.println("\t"+"\u001B[31m"+"No arguments or too many were imputed."+"\u001B[0m");
			System.out.println("\tProceeding to manual input...");
			System.out.println("Please type the file name:");
			Scanner sc = new Scanner(System.in);
			filename = sc.next();
			if(!filename.endsWith(".pgm"))
				filename = filename + ".pgm";
			System.out.println("\tSelected file: "+"\u001B[36m"+filename+"\u001B[0m");
		}

		/* Testing if the file exists */

		 File f = new File(filename);
		 if(!f.exists() || f.isDirectory()) {
			 System.out.println("Steam Carving:");
			 System.out.println("\t"+"\u001B[31m"+"Fatal: File not found"+"\u001B[0m");
			 System.exit(1);
		 }


	 	/* Internal processing */
		 System.out.println("Steam Carving:");
		 System.out.println("\tStarting internal data treatment...");

		 int[][] b = SeamCarving.readpgm(filename);
		 ArrayList<Integer> a = SeamCarving.twopath(SeamCarving.toGraph(SeamCarving.interest(b)),-1,-1);
		 int[][] im = SeamCarving.supprimerSommet(b,a);

		/* Output */

		String output = filename.substring(0,filename.lastIndexOf('.')) +"_carved.pgm";
		System.out.println("Steam Carving:");
		System.out.println("\tProcess finished.");
		System.out.println("\tResult will be writen to: "+"\u001B[36m"+output+"\u001B[0m");

	   SeamCarving.writePGM(im,output);
	 }
}
