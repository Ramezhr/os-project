public class Threads implements Runnable{
    @Override
    public void run() {
        while(true)
        {
            Nqueenproject.javaF.repaint();
            try{
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
    }
}