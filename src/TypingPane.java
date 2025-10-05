import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.LinkedList;

public class TypingPane extends BorderPane implements RescaleInterface {

    private final Label timeLabel = new Label("");
    private final Label messageLabel = new Label("");
    private WordHBox[] words = new WordHBox[30];
    private final FlowPane wordSpace = new FlowPane();
    private final FlowPane timeSpace = new FlowPane();
    private final FlowPane messageSpace = new FlowPane();
    private double fontSize = 12;
    private double wordsInThisSecond;
    private double completedWords;
    private int currentLetterIndex;
    private int currentLabelIndex;
    private int correctCharacters;
    private int incorrectCharacters;
    private int missedCharacters;
    private int extraCharacters;
    private int mistakes;
    private final int additionalCharacterLimit;
    private int waveAnimationLetterIndex;
    private int waveAnimationLabelIndex;
    private boolean contAnimation;

    public TypingPane(boolean languagesRead, String selectedLan, HashMap<String, LinkedList<String>> languages, String errorMessage){

        this.setCenter(wordSpace);
        wordsInThisSecond = 0;
        completedWords = 0;
        currentLetterIndex = 0;
        currentLabelIndex = 0;
        correctCharacters = 0;
        incorrectCharacters = 0;
        missedCharacters = 0;
        extraCharacters = 0;
        mistakes = 0;
        waveAnimationLetterIndex = 0;
        waveAnimationLabelIndex = 0;
        additionalCharacterLimit = 10;

        if (languagesRead){

            messageLabel.setText("Just type the first letter to start ...");
            newWordSet(selectedLan, languages, true);

            contAnimation = true;
            Main.startWaveAnimation();

        } else {

            messageLabel.setText(errorMessage);
            contAnimation = false;

        }

        timeSpace.getChildren().add(timeLabel);
        messageSpace.getChildren().add(messageLabel);

        timeSpace.setAlignment(Pos.CENTER);
        messageSpace.setAlignment(Pos.CENTER);

        timeSpace.setPadding(new Insets(10));
        messageSpace.setPadding(new Insets(10));
        wordSpace.setPadding(new Insets(20));


        this.setTop(timeSpace);
        this.setBottom(messageSpace);


    }

    public void resetPane(){

        wordsInThisSecond = 0;
        completedWords = 0;
        currentLetterIndex = 0;
        currentLabelIndex = 0;
        correctCharacters = 0;
        incorrectCharacters = 0;
        missedCharacters = 0;
        extraCharacters = 0;
        mistakes = 0;

        messageLabel.setText("Just type the first letter to start ...");
        timeLabel.setText("");

    }

    public void newWordSet(String selectedLan, HashMap<String, LinkedList<String>> languages, boolean setSelectedWords){

        contAnimation = false;

        wordSpace.getChildren().clear();

        words = Languages.PrepareWords(selectedLan, languages, setSelectedWords);

        for (WordHBox word : words) wordSpace.getChildren().add(word);

        rescaleLabel( new Font(fontSize * 2) );

        //Updating wave animation:
        waveAnimationLabelIndex = 0;
        waveAnimationLetterIndex = 0;
        contAnimation = true;

    }

    private void paragraphFinished(String selectedLan, HashMap<String, LinkedList<String>> languages){

        currentLetterIndex = 0;
        currentLabelIndex = 0;
        newWordSet(selectedLan, languages, false);

    }

    @Override
    public void rescale(double oldWidth, double newWidth, double oldHeight, double newHeight, boolean horRescale){

        fontSize = Rescale.Font(oldWidth, newWidth,oldHeight, newHeight, horRescale, fontSize);

        rescaleLabel( new Font(fontSize * 2) );

        ((Label)timeSpace.getChildren().get(0)).setFont( new Font(fontSize * 3) );
        ((Label)messageSpace.getChildren().get(0)).setFont( new Font(fontSize) );

    }

    public void rescaleLabel(Font newFont){

        for (WordHBox word : words) word.rescale( newFont );

    }

    public void startTyping(int time){

        messageLabel.setText("");
        timeLabel.setText(time + "");

    }

    public void nextSecond(int time){

        timeLabel.setText( time + "" );
        Main.updateStatistics(completedWords, wordsInThisSecond);
        wordsInThisSecond = 0.0;

    }

    public void typedCharacter(String character){

        String labelText = ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).getText();

        if (currentLetterIndex == words[currentLabelIndex].getChildren().size() - 1){        //typing additional character

            if (labelText.length() < additionalCharacterLimit + 2){

                ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).setText(
                        labelText.substring(0, labelText.indexOf(' ')) + character + "  "
                );
                ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).setTextFill(Color.ORANGE);

                mistakes++;
                extraCharacters++;

            }

        } else {        //typing normal characters

            if ( labelText.equals( character ) ){  //characters match;

                ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).setTextFill(Color.GREEN.brighter());
                correctCharacters++;

            } else {                                //characters do not match

                ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).setTextFill(Color.RED);
                mistakes++;
                incorrectCharacters++;

            }

            double wordWeight = 1.0 / (double)(words[currentLabelIndex].getChildren().size() - 1);
            completedWords += wordWeight;
            wordsInThisSecond += wordWeight;
            currentLetterIndex++;

        }

    }

    public void typedSpace(){

        //calculating time spent on this particular word:
        Main.logTime(currentLetterIndex != words[currentLabelIndex].getChildren().size() - 1);

        if (currentLabelIndex >= 29){

            for (int i = 0; i < words[currentLabelIndex].getChildren().size() - 1 - currentLetterIndex; i++){
                missedCharacters++;
                mistakes++;
            }

            paragraphFinished(Main.getSelectedLanguage(), Main.getLanguages());

        } else {

            for (int i = 0; i < words[currentLabelIndex].getChildren().size() - 1 - currentLetterIndex; i++){
                ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex + i))).setTextFill(Color.BLACK);
                missedCharacters++;
            }

            currentLabelIndex++;
            currentLetterIndex = 0;

        }

    }

    public void typedBackspace(){

        if (currentLetterIndex > 0) {

            if (currentLetterIndex == words[currentLabelIndex].getChildren().size() - 1){       //deleting additional characters

                String labelText = ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).getText();

                if (labelText.equals("  ")){       //already deleted all the additional characters

                    currentLetterIndex--;

                    //Updating character count:
                    if (((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).getTextFill().equals(Color.RED)){
                        incorrectCharacters--;
                    } else {
                        correctCharacters--;
                    }

                    ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).setTextFill(Color.GREY);

                } else {        //there still are characters that are to be deleted in the last label

                    //Updating character count:
                    extraCharacters--;

                    ((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).setText(
                            labelText.substring(0, labelText.indexOf(" ") - 1) + "  "
                    );

                }

            } else {        //deleting normal characters

                currentLetterIndex--;

                //Updating character count:
                if (((Label)(words[currentLabelIndex].getChildren().get(currentLetterIndex))).getTextFill().equals(Color.RED)){
                    incorrectCharacters--;
                } else {
                    correctCharacters--;
                }

                ((Label) (words[currentLabelIndex].getChildren().get(currentLetterIndex))).setTextFill(Color.GREY);

            }

        }

    }

    public double getCompletedWords(){
        return completedWords;
    }

    public int[] getLetterData(){

        //in given format: [0] -> mistakes, [1] -> correct, [2] -> incorrect, [3] -> extra, [4] -> missed

        return new int[]{mistakes, correctCharacters, incorrectCharacters, extraCharacters, missedCharacters};

    }

    public void setMessageLabelText(String text, boolean def, boolean typing){

        messageLabel.setText(def ? (typing ? "" : "Just type the first letter to start ...") : text);

    }

    public void setPause(boolean paused){

        if (paused){

            timeLabel.setText( timeLabel.getText() + " (Paused)" );

        } else {

            timeLabel.setText( timeLabel.getText().substring(0, timeLabel.getText().indexOf(" ")) );

        }

    }

    public void waveAnimation(){

        if (contAnimation) {

            WaveAnimation waveAnimation = new WaveAnimation((Label) words[waveAnimationLabelIndex].getChildren().get(waveAnimationLetterIndex));
            waveAnimation.playAnimation();

            //Updating indexes:
            waveAnimationLetterIndex++;

            if (waveAnimationLetterIndex >= words[waveAnimationLabelIndex].getChildren().size()) {
                waveAnimationLabelIndex++;
                waveAnimationLetterIndex = 0;
            }

            if (waveAnimationLabelIndex >= 30)
                waveAnimationLabelIndex = 0;

        }

    }

}
