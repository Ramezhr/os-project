import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Nqueenproject extends JPanel {
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    static int activeQueenBoard[][]=new int[Algorithm.n][Algorithm.n];
    static int currentHKey=-1;
    static Thread SearchThread;
    static boolean firstTime=true;
    static int border=20;//the amount of empty space around the frame
    static double squareSize;//the size of a chess board square
    static JFrame javaF=new JFrame("NQueens by ramzy ashraf");//must be declared as static so that other class' can repaint
    static Nqueenproject javaUI=new Nqueenproject();//must be declared as static so that other class' can repaint
    public static void main(String[] args) {
        javaF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        javaF.add(javaUI);
        javaF.setSize(750, 512);
        javaF.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-javaF.getWidth())/2,
                (Toolkit.getDefaultToolkit().getScreenSize().height-javaF.getHeight())/2);
        javaF.setVisible(true);
        squareSize=(double)(Math.min(javaUI.getHeight(), javaUI.getWidth()-250-border)-2*border)/Algorithm.n;
        Thread TimerThread=new Thread(new Threads());
        TimerThread.start();
    }
    @Override
    public void paintComponent(Graphics g) {
        if (firstTime) {firstTime=false; computerThink();}
        super.paintComponent(g);
        this.setBackground(new Color(255, 255, 255));
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getID()==KeyEvent.KEY_PRESSED) {
                            if (!Algorithm.HMapSolutions.isEmpty()) {
                                if (e.getKeyCode()==KeyEvent.VK_DOWN) {
                                    int length=Algorithm.HMapSolutions.size();
                                    if (length-1==currentHKey) {
                                        currentHKey=0;
                                        getSolution();
                                    } else {
                                        currentHKey++;
                                        getSolution();
                                    }
                                }
                                if (e.getKeyCode()==KeyEvent.VK_UP) {
                                    int length=Algorithm.HMapSolutions.size();
                                    if (currentHKey==0) {
                                        currentHKey=length-1;
                                        getSolution();
                                    } else {
                                        currentHKey--;
                                        getSolution();
                                    }
                                }
                            }
                            if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
                                Algorithm.n++;
                                newDimension();
                            }
                            if (e.getKeyCode()==KeyEvent.VK_LEFT) {
                                if (Algorithm.n>4) {Algorithm.n--; newDimension();}
                            }
                        }
                        return true;
                    }
                });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                squareSize=(double)(Math.min(getHeight(), getWidth()-250-border)-2*border)/Algorithm.n;
            }
        });
        if (Algorithm.n%2==0) {
            for (int i=0;i<Algorithm.n*Algorithm.n;i+=2) {//draw chess board
                g.setColor(new Color(0, 0, 0));
                g.fillRect((int)((i%Algorithm.n+(i/Algorithm.n)%2)*squareSize)+border, (int)((i/Algorithm.n)*squareSize)+border, (int)squareSize, (int)squareSize);
                g.setColor(new Color(255, 255, 255));
                g.fillRect((int)(((i+1)%Algorithm.n-((i+1)/Algorithm.n)%2)*squareSize)+border, (int)(((i+1)/Algorithm.n)*squareSize)+border, (int)squareSize, (int)squareSize);
            }
        } else {
            for (int i=0;i<Algorithm.n*Algorithm.n-1;i+=2) {//draw chess board
                g.setColor(new Color(0, 0, 0));
                g.fillRect((int)((i%Algorithm.n)*squareSize)+border, (int)((i/Algorithm.n)*squareSize)+border, (int)squareSize, (int)squareSize);
                g.setColor(new Color(255, 255, 255));
                g.fillRect((int)(((i+1)%Algorithm.n)*squareSize)+border, (int)(((i+1)/Algorithm.n)*squareSize)+border, (int)squareSize, (int)squareSize);
            }
            int i=Algorithm.n*Algorithm.n-1;
            g.setColor(new Color(0, 0, 0));
            g.fillRect((int)((i%Algorithm.n)*squareSize)+border, (int)((i/Algorithm.n)*squareSize)+border, (int)squareSize, (int)squareSize);
        }
        g.setColor(new Color(0, 0, 0));
        g.fill3DRect(0, border, border, (int)(Algorithm.n*squareSize), true);
        g.fill3DRect((int)(Algorithm.n*squareSize)+border, border, border, (int)(Algorithm.n*squareSize), true);
        g.fill3DRect(border, 0, (int)(Algorithm.n*squareSize), border, true);
        g.fill3DRect(border, (int)(Algorithm.n*squareSize)+border, (int)(Algorithm.n*squareSize), border, true);
        g.setColor(Color.WHITE);
        g.fill3DRect(0, 0, border, border, true);
        g.fill3DRect((int)(Algorithm.n*squareSize)+border, 0, border, border, true);
        g.fill3DRect(0, (int)(Algorithm.n*squareSize)+border, border, border, true);
        g.fill3DRect((int)(Algorithm.n*squareSize)+border, (int)(Algorithm.n*squareSize)+border, border, border, true);
        
        Image chessPieceImage;
        chessPieceImage=new ImageIcon(System.getProperty("user.dir")+"\\ChessPieces.png").getImage();
        for (int i=0;i<Algorithm.n*Algorithm.n;i++) {
            int j=-1,k=-1;
            if (activeQueenBoard[i/Algorithm.n][i%Algorithm.n]==1) {j=1;k=0;}//k=1 is the other colored queen
            if (j!=-1 && k!=-1) {
                g.drawImage(chessPieceImage, (int)((i%Algorithm.n)*squareSize)+border, (int)((i/Algorithm.n)*squareSize)+border, (int)((i%Algorithm.n+1)*squareSize)+border, (int)((i/Algorithm.n+1)*squareSize)+border, j*64, k*64, (j+1)*64, (k+1)*64, this);
            }
        }
        g.setColor(Color.BLACK);
        Font fontDepth=new Font("plIN", Font.PLAIN, 20);
        g.setFont(fontDepth);
        int x=(int)(Algorithm.n*squareSize)+2*border+10;
        int y=border+10;
        String intType;
        switch (currentHKey+1) {//in order of likelyhood of that piece being selected
            case 1: intType="st";
                break;
            case 2: intType="nd";
                break;
            case 3: intType="rd";
                break;
            default: intType="th";
                break;
        }
        g.drawString("CASES",x+80,y);
        g.drawString("1) Grid size: "+Algorithm.n+"x"+Algorithm.n,x,y+2*g.getFont().getSize());
        if (Algorithm.doneSearch) {
            g.drawString("2) There are "+Algorithm.progress+" solutions.",x,y+3*g.getFont().getSize());
//            g.drawString("3) "+Algorithm.totalNodes+" positions were",x,y+4*g.getFont().getSize());
//            g.drawString("considered.",x,y+5*g.getFont().getSize());
            g.drawString("3) That took "+((Algorithm.endTime-Algorithm.startTime)/1000)+" seconds.",x,y+5*g.getFont().getSize());
            //g.drawString("6) There are "+Algorithm.HMapUnique.size()+" unique solutions.",x,y+17*g.getFont().getSize());
        } else {
            g.drawString("2) Currently, "+Algorithm.progress+" solutions",x,y+3*g.getFont().getSize());
            g.drawString("have been found.",x,y+4*g.getFont().getSize());
//            g.drawString("3) "+Algorithm.totalNodes+" positions have",x,y+5*g.getFont().getSize());
//            g.drawString("been considered.",x,y+6*g.getFont().getSize());
            g.drawString("3) "+((System.currentTimeMillis()-Algorithm.startTime)/1000)+" seconds",x,y+5*g.getFont().getSize());
            g.drawString("have elapsed so far.",x,y+6*g.getFont().getSize());
        }
        g.drawString("4) You are currently looking at",x,y+7*g.getFont().getSize());
        g.drawString("the "+(currentHKey+1)+intType+" solution.",x,y+8*g.getFont().getSize());
        
        g.setColor(Color.black);
        g.drawString("UP/DOWN arrow keys",x,y+10*g.getFont().getSize());
        g.drawString("navigate through solutions",x,y+11*g.getFont().getSize());
        g.drawString("RIGHT/LEFT arrow keys",x,y+12*g.getFont().getSize());
        g.drawString("adjust grid size",x,y+13*g.getFont().getSize());
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        jButton2.setText("UP");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }

            private void jButton2ActionPerformed(ActionEvent evt) {
                int length=Algorithm.HMapSolutions.size();
                                    if (currentHKey==0) {
                                        currentHKey=length-1;
                                        getSolution();
                                    } else {
                                        currentHKey--;
                                        getSolution();
                                    }
            }
        });

        jButton3.setText("RIGHT");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }

            private void jButton3ActionPerformed(ActionEvent evt) {
               Algorithm.n++;
               newDimension();
            }
        });

        jButton4.setText("LEFT");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }

            private void jButton4ActionPerformed(ActionEvent evt) {
                 if (Algorithm.n>4) {Algorithm.n--; newDimension();}
            }
        });

        jButton5.setText("DOWN");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }

            private void jButton5ActionPerformed(ActionEvent evt) {
                int length=Algorithm.HMapSolutions.size();
                                    if (length-1==currentHKey) {
                                        currentHKey=0;
                                        getSolution();
                                    } else {
                                        currentHKey++;
                                        getSolution();
                                    }
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(504, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(jButton5))
                .addGap(46, 46, 46))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(242, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap())
        );
    }
    public static void computerThink() {
        activeQueenBoard=new int[Algorithm.n][Algorithm.n];
        SearchThread=new Thread(new Algorithm());
        SearchThread.start();
        javaF.repaint();
    }
    public static void newDimension() {
        computerThink();
        squareSize=(double)(Math.min(javaUI.getHeight(), javaUI.getWidth()-250-border)-2*border)/Algorithm.n;
    }
    public static void getSolution() {
        activeQueenBoard=(int[][])Algorithm.HMapSolutions.get(currentHKey);
        javaF.repaint();
    }
}


