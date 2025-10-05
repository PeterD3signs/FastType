import javafx.application.Platform;

public class TimerThread extends Thread{

    private boolean keepRunning;
    private boolean pause;

    public TimerThread(){

        keepRunning = true;
        pause = false;

    }

    public void run(){

        while (keepRunning){

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Platform.runLater(() -> Main.setMessageLabel(e.getLocalizedMessage(), true));
            }

            if(!pause && keepRunning){

                Platform.runLater(() -> Main.nextSecond());

            }

        }

    }

    public void stopRunning(){
        keepRunning = false;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

}
