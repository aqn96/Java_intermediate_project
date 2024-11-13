package games;

import graphics.G;
import graphics.WinApp;
import music.UC;

import java.awt.*;

public class Maze extends WinApp {
  public static final int xSize = 1000, ySize = 800;
  public static final int xM = 50, yM = 50, C = 30;
  public static final int W = (xSize-2*xM)/C, H = (ySize-2*yM)/C;
  static int[] next = new int[W+1], prev = new int[W+1];
  public static int y;
  public static Graphics gg;
  public Maze(){
    super("Maze",xSize, ySize);
  }

  public void paintComponent(Graphics g){
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.BLACK);
    gg = g;
    hRowZero();
    mid();
    vLast();
    hLast();
  }
  public int x(int i){return xM+i*C;}
  // hLine - horizontal line
  public void hLine(int i){gg.drawLine(x(i), y, x(i+1), y); merge(i, i+1);}
  public void vLine(int i){gg.drawLine(x(i), y, x(i), y+C);}
  public void merge(int i, int j){
    int pi = prev[i];
    int pj = prev[j];
    next[pj] = i;
    next[pi] = j;
    prev[i] = pj;
    prev[j] = pi;
  }
  public static void split(int i){
    int pi = prev[i], ni = next[i];
    next[pi] = ni;
    prev[ni] = pi;
    next[i] = i;
    prev[i] = i;
  }
  public void singletonCycle(int i){
    next[i] = i;
    prev[i] = i;
  }
  public boolean sameCycle(int i, int j){
    int n = next[i];
    while(n != i){
      if(n == j) return true;
      n = next[n];
    }
    return false;
  }
  public static boolean pV(){return G.rnd(100) < 33;}
  public static boolean pH(){return G.rnd(100) < 47;}
  public void hRule(int i){
    if(!sameCycle(i, i+1) && pH()){hLine(i);}
  }
  public void vRule(int i){
    if(next[i] == i || pV()){
      vLine(i);
    }
    else{
      noVLine(i);
    }
  }
  public static void noVLine(int i){
    split(i);
  }
  public void hRow(){for(int i = 0; i < W; i++){hRule(i);}}
  public void vRow(){vLine(0); for(int i = 1; i < W; i++){vRule(i);} vLine(W);}
  public void hRowZero(){
    y = yM;
    singletonCycle(0);
    for(int i = 0; i < W; i++){
      singletonCycle(i+1);
      hLine(i);
    }
  }
  public void mid(){
    for(int i = 0; i < H-1; i++){
      vRow();
      y+=C;
      hRow();
    }
  }
  public void vLast(){
    vLine(0);
    vLine(W);
    for(int i = 1; i < W; i++){
      if(!sameCycle(i, 0)){
        merge(i, 0);
        vLine(i);
      }
    }
  }
  public void hLast(){
    y+=C;
    for(int i = 0; i < W; i++){
      hLine(i);
    }
  }
  public static void main(String[] args){
    PANEL = new Maze();
    WinApp.launch();
  }
}
