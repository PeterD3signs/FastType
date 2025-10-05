import javafx.application.Platform;

public class MessageThread extends Thread{

    private boolean keepRunning;
    private int timePassed;
    private boolean countTime;

    public MessageThread(){

        keepRunning = true;
        countTime = false;
        timePassed = 0;

    }

    public void run(){

        while (keepRunning){

            try {
                sleep(100);
            } catch (InterruptedException e) {
                Platform.runLater(() -> Main.setMessageLabel(e.getLocalizedMessage(), true));
            }

            if (countTime) {

                timePassed += 100;

                if(timePassed >= 5000){

                    Platform.runLater(() -> Main.resetMessageLabel());
                    countTime = false;

                }

            }


        }

    }

    public void stopRunning(){
        keepRunning = false;
    }

    public void startCounting() {

        countTime = true;
        timePassed = 0;

    }

}
