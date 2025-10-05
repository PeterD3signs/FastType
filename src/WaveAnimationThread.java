import javafx.application.Platform;

public class WaveAnimationThread extends Thread{

    private boolean keepRunning;

    public WaveAnimationThread(){

        keepRunning = true;

    }

    public void run(){

        while (keepRunning){

            try {
                sleep(100);
            } catch (InterruptedException e) {
                Platform.runLater(() -> Main.setMessageLabel(e.getLocalizedMessage(), true));
            }

            if(keepRunning){

                Platform.runLater(() -> Main.contWave());

            }

        }

    }

    public void stopRunning(){
        keepRunning = false;
    }

}
