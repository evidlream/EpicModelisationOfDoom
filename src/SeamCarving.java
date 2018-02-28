import java.util.ArrayList;
import java.io.*;
import java.util.*;
public class SeamCarving
{
	
	public static int[][] interest(int[][] image){
		int[][] res = new int[image.length][image[0].length];
		for(int i = 0;i < res.length;i++){
			for(int j =0;j < res[0].length;j++){
				int diff = 0;
				if(j >0) diff += image[i][j-1];
				if(j < res[0].length-1) diff += image[i][j+1];
				diff = image[i][j] - (diff);
				if(diff < 0) diff = -diff;
				res[i][j] = diff;
			}
		}
		
		return res;
	}

   public static int[][] readpgm(String fn)
	 {		
        try {
            InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(fn);
            BufferedReader d = new BufferedReader(new InputStreamReader(f));
            String magic = d.readLine();
            String line = d.readLine();
		   while (line.startsWith("#")) {
			  line = d.readLine();
		   }
		   Scanner s = new Scanner(line);
		   int width = s.nextInt();
		   int height = s.nextInt();		   
		   line = d.readLine();
		   s = new Scanner(line);
		   int maxVal = s.nextInt();
		   int[][] im = new int[height][width];
		   s = new Scanner(d);
		   int count = 0;
		   while (count < height*width) {
			  im[count / width][count % width] = s.nextInt();
			  count++;
		   }
		   return im;
        }
		
        catch(Throwable t) {
            t.printStackTrace(System.err) ;
            return null;
        }
    }
	
	public static void writePGM(int[][] image, String name){
		try{
		    PrintWriter ecriture = new PrintWriter(name);
		    int largeur = image[0].length;
		    int hauteur = image.length;
		    
		    //entete
		    ecriture.println("P2");
		    ecriture.println(largeur + " " + hauteur);
		    ecriture.println(255);
		    
		    //contenu
		    int tailleLigne = 0;
		    int val = 0;
		    String temp = "";
		    int tailleCourante = 0;
		    for (int i = 0; i < hauteur; ++i)
		    {
		      for (int j = 0; j < largeur; ++j)
		      {
		        val = image[i][j];
		        temp = "" + val;
		        tailleCourante = temp.length() + 1;
		        if (tailleCourante + tailleLigne > 70)
		        {
		          ecriture.println();
		          tailleLigne = 0;
		        }
		        tailleLigne += tailleCourante;
		        ecriture.print(val + " ");
		      }
		    }
		    ecriture.close(); 
		}
		catch(Exception e){
			System.out.println("Erreur ecriture du fichier");
		}
	}
	
	public static Graph toGraph(int[][] intr) {
	       int n = intr.length;
	       int m = intr[0].length;
	       int i,j;
	       Graph g = new Graph(n*m+2);

	       for(i =0;i < n-1;i++){
	       		for(j =0;j < m;j++){
	       			g.addEdge(new Edge(j+m*i,j+m*(i+1),intr[i][j]));
	       			if(j > 0)
	       				g.addEdge(new Edge(j+m*i,j+m*(i+1)-1,intr[i][j]));
	       			if(j < m-1)
	       				g.addEdge(new Edge(j+m*i,j+m*(i+1)+1,intr[i][j]));
				}
		   }

		//on fait les arrete des derniers sommet du graph
		for (j = 0; j < m ; j++) {
			g.addEdge(new Edge((n-1)*m+j, n * m, intr[n - 1][j]));
		}
		// on met les arrete de base � premi�re ligne � 0
		for (j = 0; j < m ; j++)
			g.addEdge(new Edge(n*m+1, j, 0));

	    return g;
	   }
	
	public static ArrayList<Integer> dijkstra(Graph g){

		int vertices = g.vertices();
		int[][] distances = new int[vertices][2];
		//initialisation tableau
		for(int i = 0; i < distances.length;i++){
			//cout/distance
			distances[i][0] = Integer.MAX_VALUE;
			//predecesseur
			distances[i][1] = -1;
		}
		// initialisation racine
		distances[vertices-1][0] = 0;

		//initialisation de la file de priorité
		Heap heap = new Heap(vertices);
		heap.decreaseKey(vertices-1,0);

		int element;
		Iterator voisins;
		int v;
		Edge tmp;

		while(!heap.isEmpty()){
			//recuperation de l'element courant
			element = heap.pop();

			voisins = g.adj(element).iterator();
			while(voisins.hasNext()){
				tmp = (Edge)(voisins.next());
				// mise a jour des distances
				if(distances[tmp.to][0] > distances[tmp.from][0]+tmp.cost){
					distances[tmp.to][0] = distances[tmp.from][0]+tmp.cost;
					heap.decreaseKey(tmp.to,distances[tmp.to][0]);
					//sauvegarde predecesseur
					distances[tmp.to][1] = tmp.from;
				}
			}
		}

		// on initialise k au noeud de fin
		int k = vertices-2;
		ArrayList<Integer> res = new ArrayList();
		//tant qu'on est pas a la racine on ajoute le predecesseur a la liste
		while(k != vertices-1){
			k = distances[k][1];
			res.add(k);
		}
		//on supprime la racine de la liste
		res.remove(res.size()-1);

		return res;
	}

	public static int[][] supChemin(int[][] b,ArrayList<Integer> a){
		int [][] n = new int[b.length][b[0].length-1];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				if (a.get(a.size()-1-i) != i*b.length+j && j < b[0].length-1) {
					n[i][j] = b[i][j];
				}
				else{
					System.out.println(a.get(a.size()-1-i));
					for(int h =j+1;h < b[0].length;h++)
						n[i][h-1] = b[i][h];
				}
			}
		}
		return n;
	}

   
}
