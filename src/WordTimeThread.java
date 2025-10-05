import javafx.application.Platform;

import java.util.LinkedList;

public class WordTimeThread extends Thread{

    private boolean keepRunning;
    private long passedTime;
    private boolean pause;
    private boolean logTime;
    private boolean wordSkipped;
    private long startCalTime;
    private boolean justStarted;
    private boolean showResultPane;
    private final LinkedList<Long> timePerWord;

    public WordTimeThread(){

        keepRunning = true;
        passedTime = 0;
        pause = false;
        logTime = false;
        wordSkipped = false;
        justStarted = true;
        showResultPane = true;
        timePerWord = new LinkedList<>();

    }

    public void run(){

        while (keepRunning){

            if(justStarted) {
                startCalTime = System.currentTimeMillis();
                justStarted = false;
            }

            if(logTime){

                timePerWord.add( wordSkipped ? 0 : passedTime );

                logTime = false;
                wordSkipped = false;
                passedTime = 0;

            }

            if(!pause)
                passedTime += 10;

            long sleepForMillis = 10 - (System.currentTimeMillis() - startCalTime);

            try {
                sleep(sleepForMillis);
            } catch (InterruptedException e) {
                Platform.runLater(() -> Main.setMessageLabel(e.getLocalizedMessage(), true));
            }

            startCalTime = System.currentTimeMillis();

        }

        if (showResultPane)
            Platform.runLater(() -> Main.endTest(timePerWord));

    }

    public void stopRunning(boolean showResultPane){
        keepRunning = false;
        this.showResultPane = showResultPane;
    }

    public void logTime(boolean wordSkipped) {
        logTime = true;
        this.wordSkipped = wordSkipped;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

}
