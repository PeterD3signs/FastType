import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class BottomHBox extends HBox implements RescaleInterface {

    private double fontSize = 12;
    private final Label[] restartLabels = new Label[]{
            new Label(" Tab "),
            new Label("+"),
            new Label(" Enter "),
            new Label("-"),
            new Label("restart test")
    };
    private final Label[] pauseLabels = new Label[]{
            new Label(" Ctrl "),
            new Label("+"),
            new Label(" Shift "),
            new Label("+"),
            new Label(" P "),
            new Label("-"),
            new Label("pause")
    };
    private final Label[] endTestLabels = new Label[]{
            new Label(" Esc "),
            new Label("-"),
            new Label("end test")
    };


    private final FadeTransition restartTransition;
    private final FadeTransition pauseTransition;
    private final FadeTransition endTestTransition;


    public BottomHBox() {

        this.setAlignment(Pos.CENTER);

        //HBoxes:
        HBox restartHBox = new HBox();
        HBox pauseHBox = new HBox();
        HBox endTestHBox = new HBox();

        restartHBox.setPadding(new Insets(10, 30, 10, 30));
        pauseHBox.setPadding(new Insets(10, 30, 10, 30));
        endTestHBox.setPadding(new Insets(10, 30, 10, 30));

        restartHBox.setSpacing(10);
        pauseHBox.setSpacing(10);
        endTestHBox.setSpacing(10);

        //Animations:
        restartTransition = new FadeTransition(Duration.millis(100), restartHBox);
        restartTransition.setFromValue(1.0);
        restartTransition.setToValue(0.0);
        restartTransition.setCycleCount(2);
        restartTransition.setAutoReverse(true);

        pauseTransition = new FadeTransition(Duration.millis(100), pauseHBox);
        pauseTransition.setFromValue(1.0);
        pauseTransition.setToValue(0.0);
        pauseTransition.setCycleCount(2);
        pauseTransition.setAutoReverse(true);

        endTestTransition = new FadeTransition(Duration.millis(100), endTestHBox);
        endTestTransition.setFromValue(1.0);
        endTestTransition.setToValue(0.0);
        endTestTransition.setCycleCount(2);
        endTestTransition.setAutoReverse(true);

        //Labels formatting:
        restartLabels[0].setBorder( new Border( new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT ) ) );
        restartLabels[2].setBorder( new Border( new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT ) ) );
        pauseLabels[0].setBorder( new Border( new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT ) ) );
        pauseLabels[2].setBorder( new Border( new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT ) ) );
        pauseLabels[4].setBorder( new Border( new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT ) ) );
        endTestLabels[0].setBorder( new Border( new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT ) ) );

        //Adding labels:
        for (Label l : restartLabels) restartHBox.getChildren().add(l);
        for (Label l : pauseLabels) pauseHBox.getChildren().add(l);
        for (Label l : endTestLabels) endTestHBox.getChildren().add(l);

        //Adding HBoxes:
        this.getChildren().addAll(restartHBox, pauseHBox, endTestHBox);

    }

    @Override
    public void rescale(double oldWidth, double newWidth, double oldHeight, double newHeight, boolean horRescale){

        fontSize = Rescale.Font(oldWidth, newWidth,oldHeight, newHeight, horRescale, fontSize);

        Font newFont = new Font(fontSize);

        for (Label l : restartLabels) l.setFont( newFont );
        for (Label l : pauseLabels) l.setFont( newFont );
        for (Label l : endTestLabels) l.setFont( newFont );

    }

    public void restartPressed(){

        restartTransition.play();

    }

    public void pausePressed(){

        pauseTransition.play();

    }

    public void endTestPressed(){

        endTestTransition.play();

    }

}
