package games;
import graphics.G;
import graphics.WinApp;
import java.awt.*;

// Eller's Algorithm very similar to Neural Networks (Big(O) is n^2 using linked list
// but Union-Find algorithm can get it to O('acker) (inverse of Ackermann function) time)

public class Maze extends WinApp {
    public static final int xSize = 1000, ySize = 700;
    // C for cell size
    public static final int xM = 50, yM = 50, C = 30;
    public static final int W = (xSize - 2*xM )/ C, H = (ySize - 2*yM )/ C;
    public static int y;
    public static Graphics gg;

    static int[] next = new int[W+1], prev = new int[W+1]; // Connectivity information in linked list

    public Maze(){
        super("Maze", xSize, ySize);
    }

    public void paintComponent(Graphics g){
        G.bkWhite(g);
        g.setColor(Color.BLACK);
        gg = g;

        // Drawing the first line/row
        hRow0();
        // Drawing everything between the maze
        mid();
        // Drawing the last line (horizontal and vertical)
        vLast();
        hLast();
    }

    public int x(int i){return xM + (i*C);}

    // Horizontal edge from one point to another
    public void hLine(int i){gg.drawLine(x(i), y, x(i+1), y);merge(i, i+1);}
    // Vertical line
    public void vLine(int i){gg.drawLine(x(i), y, x(i), y+C);}

    // Merging two linked list
    public void merge(int i, int j){
        int pi = prev[i], pj = prev[j];
        next[pj] = i; next[pi] = j;
        prev[i] = pj; prev[j] = pi;
    }

    public void split(int i){
        int pi = prev[i], ni = next[i];
        next[pi] = ni; prev[ni] = pi;
        next[i] = i; prev[i] = i;
    }

    public void singletonCycle(int i){next[i] = i; prev[i] = i;}

    public boolean sameCycle(int i, int j){
        int n = next[i];
        while(n != i){
            if( n == j){ return true;}
            n = next[n];
        }
        return false;
    }

    // Probability function to decide if connect or not
    public static boolean pV(){return G.rnd(100) < 33;}
    public static boolean pH(){return G.rnd(100) < 47;}
    // Horizontal rule of connection
    public void hRule(int i){if (!sameCycle(i, i + 1) && pH()) {hLine(i);}}
    // Vertical rule of connection
    public void vRule(int i){
        if(next[i] == i || pV()){
            vLine(i);
        }else{
            novLine(i);
        }
    }
    // If did not draw line vertical
    public void novLine(int i){
        split(i);
    }

    // Calling hRule over and over across the row
    public void hRow(){
        for(int i = 0; i < W; i++){hRule(i);}
    }

    // Calling vRule over and over to connect vertical
    public void vRow(){
        vLine(0);
        for (int i = 1; i < W; i++){
            vRule(i);
        }
        vLine(W);
    }


    public void hRow0(){
        y = yM;
        singletonCycle(0);
        for(int i = 0; i < W; i++){
            singletonCycle(i+1);
            hLine(i);
            }
    }

    public void mid(){
        for(int i = 0; i< H-1; i++){
            vRow();
            y += C;
            hRow();
        }
    }

    public void vLast(){
        vLine(0);
        vLine(W);
        for(int i = 1; i < W; i++){
            // If line not in cycle of the wall(outside boundary) do a connection
            if(!sameCycle(i, 0)){
                merge(i, 0);
                vLine(i);
            }
        }
    }

    public void hLast(){
        y += C;
        for(int i = 0; i < W; i++){
            hLine(i);
        }
    }

    public static void main(String[] args){
        PANEL = new Maze();
        WinApp.launch();
    }

}
