import java.awt.*;
import java.util.*;
public class Algorithm implements Runnable{
    static HashMap HMapSolutions=new HashMap();
    static HashMap HMapUnique=new HashMap();
    static int progress;
    static long startTime, endTime;
    static boolean doneSearch;
    static int totalNodes;
    static int solutions;
    static int n=4;
    public static void masterController() {
        doneSearch=false;
        totalNodes=0;
        HMapSolutions.clear();
        Nqueenproject.currentHKey=-1;
        solutions=0;
        progress=0;
        guess(0,0,new int[n][n]);
        //unique();
        doneSearch=true;
    }
    public static void guess(int x, int y, int[][] queenBoard) {
        while (y<n)
        {
            queenBoard[x][y]=1;
            totalNodes++;
            if (Statutory.posibleQ(x*n+y, queenBoard)) {
                if (x+1==n) {
                    int queenBoard2[][]=new int[n][n];
                    for (int i=0;i<n;i++) {
                        System.arraycopy(queenBoard[i],0,queenBoard2[i],0,n);
                    }
                    HMapSolutions.put(progress, queenBoard2);
                    queenBoard[x][y]=0;
                    progress++;
                    if (Nqueenproject.currentHKey==-1) {
                        Nqueenproject.currentHKey=0;
                        Nqueenproject.getSolution();
                    }
                    return;
                }
                guess(x+1, 0, queenBoard);
            }
            queenBoard[x][y]=0;
            y++;
        }
    }
    public static void unique() {
        //calculates how many unique solutions there are (solutions that are rotations of each other are eliminated)
        //not in use due to the time it takes to go through all of the solutions
        HMapUnique.clear();
        int counter=0;
        int originalBoard[][];
        int rotateBoard1[][]=new int[n][n];
        int rotateBoard2[][]=new int[n][n];
        int rotateBoard3[][]=new int[n][n];
        for (int i=0;i<HMapSolutions.size();i++) {
            originalBoard=(int[][])HMapSolutions.get(i);
            for(int x=0;x<n;x++){
                for(int y=0;y<n;y++){
                    rotateBoard1[x][y]=originalBoard[n-1-y][x];
                }
            }
            for(int x=0;x<n;x++){
                for(int y=0;y<n;y++){
                    rotateBoard2[x][y]=rotateBoard1[n-1-y][x];
                }
            }
            for(int x=0;x<n;x++){
                for(int y=0;y<n;y++){
                    rotateBoard3[x][y]=rotateBoard2[n-1-y][x];
                }
            }
            int j=0;
            boolean uniqueBoard=true;
            while (j<HMapUnique.size() && uniqueBoard)
            {
                if (Arrays.deepEquals((int[][])HMapUnique.get(j), rotateBoard1) ||
                        Arrays.deepEquals((int[][])HMapUnique.get(j), rotateBoard2) ||
                        Arrays.deepEquals((int[][])HMapUnique.get(j), rotateBoard3)) {
                    uniqueBoard=false;
                }
                j++;
            }
            if (uniqueBoard) {
                int originalBoard2[][]=new int[n][n];
                for (int k=0;k<n;k++) {
                    System.arraycopy(originalBoard[k],0,originalBoard2[k],0,n);
                }
                HMapUnique.put(counter, originalBoard2);
                counter++;
            }
        }
        System.out.println(HMapUnique.size());
    }
    public static void drawToArray(int[][] queenBoard) {
        //for debugging purposes only
        for (int i=0;i<n;i++) {
            System.out.println(Arrays.toString(queenBoard[i]));
        }
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }
    @Override
    public void run() {
        Nqueenproject.javaF.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        startTime=System.currentTimeMillis();
        masterController();
        endTime=System.currentTimeMillis();
        Toolkit.getDefaultToolkit().beep();
        Nqueenproject.javaF.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Nqueenproject.javaF.repaint();
    }
}