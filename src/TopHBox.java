import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.LinkedList;

public class TopHBox extends HBox implements RescaleInterface {

    private final Label setTimeLabel = new Label("   Set time: ");
    private final ChoiceBox<String> timeCB = new ChoiceBox<>(FXCollections.observableArrayList(
            "15 s", "20 s", "45 s", "60 s", "90 s", "120 s", "300 s")
    );
    private final Label setLanLabel = new Label("Set language: ");
    private ChoiceBox<String> lanCB;
    private double fontSize = 12;


    public TopHBox(LinkedList<String> languages, String selectedLan){

        if (languages.isEmpty())
            lanCB.setDisable(true);
        else {

            lanCB = new ChoiceBox<>(FXCollections.observableList(languages));
            lanCB.setValue(selectedLan);

        }

        timeCB.setValue("45 s");

        this.getChildren().addAll(setLanLabel, lanCB, setTimeLabel, timeCB);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER);

        lanCB.setOnAction(event -> Main.setSelectedLanguage( lanCB.getValue() ));
        timeCB.setOnAction(event -> Main.setSelectedTime( Integer.parseInt( timeCB.getValue().substring(0, timeCB.getValue().indexOf(' ') ) ) ) );

    }

    public void Typing(boolean typing) {

        lanCB.setDisable(typing);
        timeCB.setDisable(typing);

    }

    @Override
    public void rescale(double oldWidth, double newWidth, double oldHeight, double newHeight, boolean horRescale){

        fontSize = Rescale.Font(oldWidth, newWidth,oldHeight, newHeight, horRescale, fontSize);

        Font newFont = new Font(fontSize);

        setTimeLabel.setFont( newFont );
        setLanLabel.setFont( newFont );
        timeCB.setStyle( "-fx-font: " + fontSize + " px;" );
        lanCB.setStyle( "-fx-font: " + fontSize + " px;" );

    }

    public void setVisibleElements(boolean visible){

        setTimeLabel.setVisible(visible);
        setLanLabel.setVisible(visible);
        timeCB.setVisible(visible);
        lanCB.setVisible(visible);

    }


}
