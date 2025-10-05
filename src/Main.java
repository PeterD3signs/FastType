import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;

public class Main extends Application implements RescaleInterface{

    private static HashMap<String, LinkedList<String>> languages;
    private static double width = 960;
    private static double height = 540;
    private static Scene scene;
    private static final BorderPane root = new BorderPane();
    private static TypingPane typingPane;
    private static ResultPane resultPane;
    private static TopHBox topHBox;
    private static BottomHBox bottomHBox;
    private static boolean languagesNonEmpty = true;
    private static String selectedLanguage;
    private static int selectedTime = 45;
    private static int spentTime = 0;
    private static boolean typing = false;
    private static boolean paused = false;
    private static boolean inTypingPane = true;
    private static WordTimeThread wordTimeThread = new WordTimeThread();
    private static TimerThread timerThread = new TimerThread();
    private static MessageThread messageThread = new MessageThread();
    private static final WaveAnimationThread waveAnimationThread = new WaveAnimationThread();
    private static LinkedList<String> selectedWords;
    private static LinkedList<Double> averageWPM;
    private static LinkedList<Double> actualWPM;
    private static boolean tabPressed = false;


    public static void main(String[] args) {

        String errorMessage = null;

        try{
            languages = Languages.ReadDictionary();
        } catch (Exception ex){
            languagesNonEmpty = false;
            errorMessage = ex.getMessage();
        }

        if (languagesNonEmpty){

            LinkedList<String> languageList = new LinkedList<>(languages.keySet());

            languageList.sort((l1, l2) -> l1.compareTo(l2));

            if (languageList.contains("english"))
                selectedLanguage = "english";
            else
                selectedLanguage = languageList.get(0);

            topHBox = new TopHBox( languageList, selectedLanguage );
            typingPane = new TypingPane( true, selectedLanguage, languages, errorMessage);

        } else {

            topHBox = new TopHBox(new LinkedList<>(), null);
            typingPane = new TypingPane(false, null, null, errorMessage);

        }

        actualWPM = new LinkedList<>();
        averageWPM = new LinkedList<>();
        actualWPM.add(0.0);
        averageWPM.add(0.0);

        bottomHBox = new BottomHBox();

        launch(args);

        if (wordTimeThread.isAlive())
            wordTimeThread.stopRunning(false);

        if(timerThread.isAlive())
            timerThread.stopRunning();

        if (messageThread.isAlive())
            messageThread.stopRunning();

        if (waveAnimationThread.isAlive())
            waveAnimationThread.stopRunning();

    }

    public static void setSelectedTime(int selectedTime) {
        Main.selectedTime = selectedTime;
    }

    public static String getSelectedLanguage() {
        return selectedLanguage;
    }

    public static void setSelectedLanguage(String selectedLanguage) {
        Main.selectedLanguage = selectedLanguage;
        typingPane.newWordSet(selectedLanguage, languages, true);

    }

    public static void addNextWordSet(LinkedList<String> selectedWords){
        Main.selectedWords.addAll(selectedWords);
    }

    public static void setSelectedWords(LinkedList<String> selectedWords) {
        Main.selectedWords = selectedWords;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        root.setTop( topHBox );
        root.setCenter( typingPane );
        root.setBottom( bottomHBox );

        scene = new Scene(root, width, height);

        //rescaling:
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {

            rescale(width, scene.getWidth(), height, scene.getHeight(), true);
            width = scene.getWidth();
            height = scene.getHeight();

        });
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {

            rescale(width, scene.getWidth(), height, scene.getHeight(), false);
            width = scene.getWidth();
            height = scene.getHeight();

        });

        //keyPressed
        scene.setOnKeyPressed(event -> {

            //pause:
            if (event.isShiftDown() && event.isControlDown() && event.getCode() == KeyCode.P){

                bottomHBox.pausePressed();

                if (inTypingPane && typing) {   //user is typing and pause is allowed

                    paused = !paused;
                    timerThread.setPause(paused);
                    wordTimeThread.setPause(paused);
                    typingPane.setPause(paused);

                } else {        //user is not typing so pause should not be allowed;

                    setMessageLabel("You can only pause while running the test.", true);

                }

            }

            //end test:
            if (event.getCode() == KeyCode.ESCAPE){

                bottomHBox.endTestPressed();

                if (inTypingPane && typing) {

                    endTyping(true);

                } else if (inTypingPane){

                    setMessageLabel("You can't end the test before starting.", true);

                } else {        //user in result pane;

                    retToTypingPane();

                }

            }

            //restart test:
            if (event.getCode() == KeyCode.ENTER && tabPressed){

                bottomHBox.restartPressed();

                if (inTypingPane && typing) {

                    resetTest(true);

                } else if (inTypingPane){

                    setMessageLabel("You can't reset the test before starting.", true);

                } else {        //user in result pane;

                    retToTypingPane();

                }

            }

            //Tab key pressed:
            if (event.getCode() == KeyCode.TAB){
                tabPressed = true;
            }

        });

        //handling Tab press
        scene.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.TAB){
                tabPressed = false;
            }

        });


        //keyTyped
        scene.setOnKeyTyped (event -> {

            String character = event.getCharacter();

            if (inTypingPane) {

                if(!typing &&
                        !event.isShiftDown() &&
                        !character.equals(KeyCode.ESCAPE.getChar()) &&
                        !character.equals(KeyCode.ENTER.getChar()) &&
                        !character.equals(KeyCode.TAB.getChar()) &&
                        !character.equals(KeyCode.SPACE.getChar()) &&
                        !tabPressed
                ){

                    typing = true;
                    topHBox.Typing(true);
                    typingPane.startTyping(selectedTime);
                    timerThread.start();
                    wordTimeThread.start();

                }

                if (typing &&
                        !event.isShiftDown() &&
                        !character.equals(KeyCode.ESCAPE.getChar()) &&
                        !character.equals(KeyCode.ENTER.getChar()) &&
                        !character.equals(KeyCode.TAB.getChar()) &&
                        !tabPressed &&
                        !paused
                ){

                    if (character.equals(KeyCode.SPACE.getChar()))
                        typingPane.typedSpace();
                    else if (character.equals(KeyCode.BACK_SPACE.getChar()))
                        typingPane.typedBackspace();
                    else
                        typingPane.typedCharacter(character);

                }

            }

        });

        primaryStage.setTitle("FastType");
        primaryStage.setScene(scene);
        primaryStage.show();



    }
    public static void nextSecond(){

        if(selectedTime - spentTime != 0) {

            spentTime++;
            typingPane.nextSecond(selectedTime - spentTime);

        }else {

            endTyping(true);

        }

    }

    public static void updateStatistics(double completedWords, double wordsInThisSecond){
        averageWPM.add(completedWords * 60 / spentTime);
        actualWPM.add(wordsInThisSecond * 60);
    }

    @Override
    public void rescale(double oldWidth, double newWidth, double oldHeight, double newHeight, boolean horRescale) {

        topHBox.rescale(oldWidth, newWidth, oldHeight, newHeight, horRescale);
        bottomHBox.rescale(oldWidth, newWidth, oldHeight, newHeight, horRescale);
        typingPane.rescale(oldWidth, newWidth, oldHeight, newHeight, horRescale);
        if (resultPane != null)
            resultPane.rescale(oldWidth, newWidth, oldHeight, newHeight, horRescale);

    }

    public static void retToTypingPane(){

        inTypingPane = true;

        resetTest(false);

        root.setCenter(typingPane);
        topHBox.setVisibleElements(true);

    }

    public static void resetTest(boolean inTypingPane){

        if (inTypingPane)
            endTyping(false);

        spentTime = 0;
        typing = false;
        topHBox.Typing(false);

        actualWPM = new LinkedList<>();
        averageWPM = new LinkedList<>();
        actualWPM.add(0.0);
        averageWPM.add(0.0);

        typingPane.newWordSet(selectedLanguage, languages, true);
        typingPane.resetPane();

    }

    public static void endTyping(boolean showResultPane){

        timerThread.stopRunning();
        timerThread = new TimerThread();

        wordTimeThread.stopRunning(showResultPane);
        wordTimeThread = new WordTimeThread();

    }

    public static void endTest(LinkedList<Long> timePerWord){

        String exceptionMessage = null;

        //calculate and save WPM per word:
        try {
            SaveData.save(timePerWord, selectedWords);
        } catch (Exception ex){
            exceptionMessage = ex.getMessage();
        }

        //launch result pane:
        inTypingPane = false;
        resultPane = new ResultPane(typingPane.getLetterData(), selectedLanguage, selectedTime, spentTime, typingPane.getCompletedWords(), averageWPM, actualWPM);
        root.setCenter(resultPane);
        topHBox.setVisibleElements(false);

        //showing potential errors:
        if (exceptionMessage != null)
            setMessageLabel(exceptionMessage, true);

    }

    public static void logTime(boolean wordSkipped){
        wordTimeThread.logTime(wordSkipped);
    }

    public static HashMap<String, LinkedList<String>> getLanguages(){

        return languages;

    }

    public static void setMessageLabel(String text, boolean withThread){

        if (inTypingPane) {

            typingPane.setMessageLabelText(text, false, typing);

            if (resultPane != null)
                resultPane.setMessageLabelText("");

        } else {

            resultPane.setMessageLabelText(text);

            typingPane.setMessageLabelText("", true, typing);

        }

        if (withThread){

            if (!messageThread.isAlive()) {
                messageThread.start();
            }

            messageThread.startCounting();

        }

    }

    public static void resetMessageLabel(){

        messageThread.stopRunning();
        messageThread = new MessageThread();

        typingPane.setMessageLabelText("",true, typing);

        if (resultPane != null)
            resultPane.setMessageLabelText("");

    }

    public static void startWaveAnimation(){

        waveAnimationThread.start();

    }

    public static void contWave(){

        if (inTypingPane)
            typingPane.waveAnimation();

    }

}