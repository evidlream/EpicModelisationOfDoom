import java.util.ArrayList;
import java.io.*;
import java.util.*;
public class SeamCarving
{
    /**
     * Fonction qui définit la valeur d'interet des différents pixels.
     * @param image Matrice contenant les valeurs de l'image
     * @return Matrice contenant les valeurs d'interet de l'ensemble des pixels de l'image
     */
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

    /**
     * Fonction convertissant une image en une matrice d'entiers correspondant aux pixels.
     * @param fn Nom du fichier
     * @return Matrice contenant les valeurs de l'image
     */
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

    /**
     * Fonction d'écriture exporte une image a partir d'une matrice contenant les valeurs de l'image
     * @param image Matrice contenant les valeurs de l'image
     * @param name Nom du fichier sortie
     */
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
            System.out.println("\u001B[31m"+"Erreur ecriture du fichier"+"\u001B[0m");
        }
    }

    /**
     * Fonction convertissant une matrice corespondant a l'interet ou a une image elle même en un Graphe
     * @param intr Matrice contenant les valeurs de l'image
     * @return Graphe sortie
     */
    public static Graph toGraph(int[][] intr) {
        int n = intr.length;
        int m = intr[0].length;
        int i,j;
        Graph g = new Graph(2+n*2*m-2*m);
        for(i =0;i < 2*n-2;i++){
            for(j = 0;j < m;j++) {
                if(i%2 == 0 || i == 0) {
                    g.addEdge(new Edge(j + m * i, j + m * (i + 1), intr[i/2][j]));
                    if (j > 0)
                        g.addEdge(new Edge(j + m * i, j + m * (i + 1) - 1, intr[i/2][j]));
                    if (j < m - 1)
                        g.addEdge(new Edge(j + m * i, j + m * (i + 1) + 1, intr[i/2][j]));
                }
                else{
                    /* Ajout des arc de poids zero (duplication des sommets) */
                    if(i < 2*n-3)
                        g.addEdge(new Edge(j+m*i,j + m * (i + 1),0));
                }
            }
        }

        /* On ajoute les arcs des derniers sommet du graphe */
        for (j = 0; j < m ; j++) {
            g.addEdge(new Edge(j+(n*2-3)*m, n*2*m-2*m, intr[n - 1][j]));
        }
        /* On ajoute les arcs de poids 0 en début du graphe */
        for (j = 0; j < m ; j++) {
            g.addEdge(new Edge(1+n*2*m-2*m, j, 0));
        }

        Iterator ite;
        Edge edge;
        int coupFrom, coupTo;
        int[][] plusCourt = dijkstra(g);

        for(int tmp =0;tmp < g.vertices();tmp++){
            ite = g.adj(tmp).iterator();
            while(ite.hasNext()){
                edge = (Edge) (ite.next());
                if(edge.from == tmp) {
                    coupFrom = plusCourt[edge.from][0];
                    coupTo = plusCourt[edge.to][0];
                    edge.cost = (edge.cost + coupFrom - coupTo);
                }
            }
        }

        return g;
    }

    /**
     * Algorithme d'analyse d'un graphe, renvoie le chemin le plus court
     * @param g Graphe a analyser
     * @return Matrice qui pour chaque sommet i, [i][0] contient le cout du sommet, et [i][1]= le prédécesseur pour le plus court chemin.
     */
    public static int[][] dijkstra(Graph g){

        int vertices = g.vertices();
        int[][] distances = new int[vertices][2];
        /* Initialisation tableau */
        for(int i = 0; i < distances.length;i++){
            // Cout / Distance
            distances[i][0] = Integer.MAX_VALUE;
            // Predecesseur
            distances[i][1] = -1;
        }

        /* initialisation racine */
        distances[vertices-1][0] = 0;

        /* Initialisation de la file de priorité */
        Heap heap = new Heap(vertices);
        heap.decreaseKey(vertices-1,0);

        int element;
        Iterator voisins;
        Edge tmp;

        while(!heap.isEmpty()){
            /* Recuperation de l'element courant */
            element = heap.pop();
            voisins = g.adj(element).iterator();
            while(voisins.hasNext()){
                tmp = (Edge)(voisins.next());
                /* Mise a jour des distances */
                if(distances[tmp.to][0] > distances[tmp.from][0]+tmp.cost && distances[tmp.from][0] != Integer.MAX_VALUE){

                    distances[tmp.to][0] = distances[tmp.from][0]+tmp.cost;
                    heap.decreaseKey(tmp.to,distances[tmp.to][0]);
                    // Sauvegarde predecesseur
                    distances[tmp.to][1] = tmp.from;
                }
            }
        }
        return distances;
    }

    /**
     * cherche le chemin le plus court avec la matrice passé en paramètre (couple valeur, predecesseur) et le supprime de la matrice b (interest)
     * @param b
     * @param distances
     * @return
     */
    public static int[][] supChemin(int[][] b,int[][] distances){


        //recuperation du chemin le plus court
        // on initialise k au noeud de fin
        int taille = (distances.length);
        int k = taille-2;
        ArrayList<Integer> a = new ArrayList();
        //tant qu'on est pas a la racine on ajoute le predecesseur a la liste
        while(k != taille-1){
            k = distances[k][1];
            a.add(k);
        }
        //on supprime la racine de la liste
        a.remove(a.size()-1);


        int [][] n = new int[b.length][b[0].length-1];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                if (a.get(a.size()-1-i) != i*b.length+j && j < b[0].length-1) {
                    n[i][j] = b[i][j];
                }
                else{
                    for(int h =j+1;h < b[0].length;h++)
                        n[i][h-1] = b[i][h];
                }
            }
        }
        return n;
    }


    public static ArrayList<Integer> twopath(Graph g, int s ,int t){

        /* ArrayList des sommets à supprimer */
        ArrayList<Integer> res = new ArrayList<>();


        /* Inversion des arcs */
        int debut = s;
        int fin = t;

        int[][] plusCourt = dijkstra(g);
        int taille = plusCourt.length;

        //par default : debut = racine, fin = "ciel"

        if(debut < 0)
            debut = taille-1;
        if(fin < 0)
            fin = taille-2;

        int k = fin;

        Iterator<Edge> ite;
        Edge edge;

        while(k != debut){
            ite = g.adj(plusCourt[k][1]).iterator();
            while(ite.hasNext()){
                edge = ite.next();
                if(edge.from == plusCourt[k][1] && edge.to == k){
                    edge.invert();
                    if(k != fin && edge.from != debut)
                        res.add(k);
                }
            }
            k = plusCourt[k][1];
        }

        //TODO: Virer ça
        for(int i =0;i < res.size();i++)
            System.out.println(res.get(i));

        /* Second appel à dijsktra */
        plusCourt = dijkstra(g);

        /* Second parcourt */
        while(k != taille-1){
            if(!res.contains(k))
                if(k != taille-2)
                    res.add(k);
            k = plusCourt[k][1];
        }

        return res;
    }


}
