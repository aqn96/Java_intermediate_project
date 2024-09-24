package games;

import graphics.G;
import graphics.WinApp;
import music.UC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class destructo extends WinApp implements ActionListener {

    public static Color[] color = {Color.LIGHT_GRAY, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.PINK};
    public static final int nC = 13, nR = 15;
    public static final int width = 60, height = 40;
    public static int xM = 100, yM = 100;
    public static int[][] grid = new int[nC][nR];
    public static Timer timer;
    public static int bricksRemaining;
    public static int nColor = 3;
    
    public destructo(){
        super("Destructo", UC.screenWidth,UC.screenHeight);
        startNewGame();
        timer = new Timer(30,this);
        timer.start();
    }

    public static void startNewGame(){
        if(nColor == color.length){nColor = 3;}
        rndColor(nColor);
        initRemaining();
        xM = 100;
        nColor++;
    }

    public void mouseClicked(MouseEvent me){
        int x = me.getX();
        int y = me.getY();
        if(x < 20 && y < 20){
            startNewGame();
            return;
        }
        if( x < xM || y < yM){return;}
        int r = r(y), c = c(x);
        if(r < nR && c < nC){crAction(c,r);}
        repaint();
    }

    public static boolean noMorePlays(){
        for(int r = 0; r < nR; r++){
            for(int c = 0; c < nC; c++){
                if(infectable(c,r)){
                    return false;
                }
            }
        }
        return true;
    }

    public static void initRemaining(){bricksRemaining = nR * nC;}
    public static void showRemaining(Graphics g){
        String str = "Bricks remaining: " + bricksRemaining;
        if(noMorePlays()){
            str += " No More Plays Left!";
        }
        g.setColor(Color.BLACK);
        g.drawString(str, 50, 25);
    }

    public void paintComponent(Graphics g){
        g.setColor(color[0]);
        g.fillRect(0,0,5000,5000);
        showGrid(g);
        bubble();
        showRemaining(g);
        if(slideCall()){
            xM += width / 2;
        }
    }

    public static void crAction(int c, int r){
    //    System.out.println("("+c+","+r+")"); // notation to combine value and string together
        if (infectable(c, r)) {
            infect(c,r,grid[c][r]);
        }
    }

    public static void rndColor(int k){
        for(int c = 0; c < nC; c++){
            for(int r = 0; r < nR; r++){
                grid[c][r] = 1 + G.rnd(k);
            }
        }
    }

    public static void showGrid(Graphics g) {
        for (int c = 0; c < nC; c++) {
            for (int r = 0; r < nR; r++) {
                g.setColor(color[grid[c][r]]);
                g.fillRect(x(c), y(r), width, height);
            }
        }
    }

    public static boolean infectable(int c, int  r){
        int v = grid[c][r];
        if(v == 0){return false;}
        if(r > 0){if(grid[c][r-1] == v) {return true;}}
        if(c > 0){if(grid[c-1][r] == v) {return true;}}
        if(r < nR - 1){if(grid[c][r+1] == v) {return true;}}
        if(c < nC - 1){if(grid[c+1][r] == v) {return true;}}
        return false;
    }
    public static void infect(int c, int r, int v){
        if(grid[c][r] != v){return;}
        grid[c][r] = 0;
        bricksRemaining--;
        if(r > 0){infect(c,r-1,v);}
        if(c > 0){infect(c-1,r,v);}
        if(r < nR-1){infect(c,r+1,v);}
        if(c < nC-1){infect(c+1,r,v);}
    }
    public static int rowCanBubble(int c){
        for(int r = nR-1; r > 0; r--) {
            if (grid[c][r] == 0 && grid[c][r - 1] != 0) {
                return r;
            }
        }
        return nR;
    }

    public static void bubble(){
        for(int c = 0; c < nC; c++){
            int r = rowCanBubble(c);
            if(r < nR){
                grid[c][r] = grid[c][r - 1];
                grid[c][r-1] = 0;
            }
        }
    }
    public boolean callIsEmpty(int c){
        for(int r = 0; r < nR; r++){
            if(grid[c][r] != 0){return false;}
        }
        return true;
    }

    public void swapCall(int c){  // c is none empty, c - 1 is empty.
        for(int r = 0; r < nR; r++){
            grid[c-1][r] = grid[c][r];
            grid[c][r] = 0;
        }
    }

    public boolean slideCall(){
        boolean res = false;
        for(int c = 1; c < nC; c++){
            if(callIsEmpty(c-1) && !callIsEmpty(c)){
                swapCall(c); res = true;
            }
        }
        return res;
    }

    public static int x(int c){return xM + c*width;}
    public static int y(int r){return yM + r*height;}
    public static int c(int x){return (x-xM)/width;} // Unsafe: fix in mouseClick
    public static int r(int y){return (y-yM)/height;} // Unsafe: fix in mouseClick
    public static void main(String[] args){PANEL = new destructo(); WinApp.launch();}

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
