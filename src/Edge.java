class Edge
{
    int from;
    int to;
    int cost;
    Edge(int x, int y, int cost)
    {
        this.from = x;
        this.to = y;
        this.cost = cost;
    }

    /**
     * Fonction d'inversion du sens d'un arc
     */
    public void invert(){
        int tmp = from;
        from = to;
        to = tmp;
    }

}
