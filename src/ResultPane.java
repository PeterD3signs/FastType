import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.LinkedList;

public class ResultPane extends BorderPane implements RescaleInterface{

    private final Label messageLabel = new Label("");
    private final HBox testDataSpace = new HBox();
    private final FlowPane messageSpace = new FlowPane();
    private double fontSize = 12;

    public ResultPane(int[] letterData, String selectedLan, int time, int finalizedTime, double completedWords, LinkedList<Double> averageWPM, LinkedList<Double> actualWPM){

        //DATA:
        // letterData in given format: [0] -> mistakes, [1] -> correct, [2] -> incorrect, [3] -> extra, [4] -> missed

        Label WPMTextLabel = new Label("wpm:");
        Label WPMDataLabel = new Label("");
        Label ACCTextLabel = new Label("acc:");
        Label ACCDataLabel = new Label("");
        Label TimeTextLabel = new Label("time:");
        Label TimeDataLabel = new Label("");
        Label LangTextLabel = new Label("lang:");
        Label LangDataLabel = new Label("");
        Label CharsTextLabel = new Label("chars:");
        Label CharsDataLabel = new Label("");

        VBox WPMVBox = new VBox();
        VBox ACCVBox = new VBox();
        VBox TimeVBox = new VBox();
        VBox LangVBox = new VBox();
        VBox CharsVBox = new VBox();

        WPMDataLabel.setText( String.format("%.2f" , 60.0 * completedWords / (double)finalizedTime) );
        ACCDataLabel.setText( String.format("%.2f" , (((double)letterData[1] / (double)(letterData[1] + letterData[2] + letterData[3] + letterData[4])) * 100.0 )) + "%");
        TimeDataLabel.setText( time + "s" );
        LangDataLabel.setText( selectedLan );
        CharsDataLabel.setText( letterData[1] + "/" + letterData[2] + "/" + letterData[3] + "/" + letterData[4] );

        WPMVBox.getChildren().addAll(WPMTextLabel, WPMDataLabel);
        ACCVBox.getChildren().addAll(ACCTextLabel, ACCDataLabel);
        TimeVBox.getChildren().addAll(TimeTextLabel, TimeDataLabel);
        LangVBox.getChildren().addAll(LangTextLabel, LangDataLabel);
        CharsVBox.getChildren().addAll(CharsTextLabel, CharsDataLabel);

        testDataSpace.getChildren().addAll(WPMVBox, ACCVBox, TimeVBox, LangVBox, CharsVBox);
        messageSpace.getChildren().add(messageLabel);

        testDataSpace.setAlignment(Pos.CENTER);
        messageSpace.setAlignment(Pos.CENTER);

        testDataSpace.setPadding(new Insets(10));
        messageSpace.setPadding(new Insets(40, 10, 10, 10));

        testDataSpace.setSpacing(30);

        this.setTop(testDataSpace);
        this.setBottom(messageSpace);

        //font size:
        for (int i = 0; i < testDataSpace.getChildren().size(); i++){

            ((Label)((VBox)(testDataSpace.getChildren().get(i))).getChildren().get(1)).setFont( new Font(fontSize * 3) );

        }

        //color animation:
        Animation animation = new Transition() {

            Color color = new Color(0, 0, 1, 0.7);
            int i = 0;

            {
                setCycleDuration(Duration.seconds(10000));
                setCycleCount(Animation.INDEFINITE);
            }

            @Override
            protected void interpolate(double v) {

                double red = color.getRed();
                double green = color.getGreen();
                double blue = color.getBlue();

                if ( i < 100 ) {

                    red = red + 0.01;
                    if (red > 1.0)
                        red = 1.0;

                    green = 0.0;

                    blue = blue - 0.01;
                    if (blue < 0.0)
                        blue = 0.0;

                } else if ( i < 200 ) {

                    red = red - 0.01;
                    if (red < 0.0)
                        red = 0.0;

                    green = green + 0.01;
                    if (green > 1.0)
                        green = 1.0;

                    blue = 0.0;

                }else {

                    red = 0.0;

                    green = green - 0.01;
                    if (green < 0.0)
                        green = 0.0;

                    blue = blue + 0.01;
                    if (blue > 1.0)
                        blue = 1.0;

                }

                i++;

                if (i >= 300)
                    i = 0;

                color = new Color( red, green, blue, 0.7 );

                WPMDataLabel.setTextFill( color );
                ACCDataLabel.setTextFill( color );
                TimeDataLabel.setTextFill( color );
                LangDataLabel.setTextFill( color );
                CharsDataLabel.setTextFill( color );

            }

        };


        //GRAPH:

        //defining the axes:
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("seconds");
        yAxis.setLabel("words per minute");

        //creating the chart:
        LineChart<Number, Number> chart = new LineChart<>(xAxis,yAxis);

        //defining series::
        XYChart.Series<Number, Number> actual = new XYChart.Series<>();
        actual.setName("actual");

        XYChart.Series<Number, Number> average = new XYChart.Series<>();
        average.setName("average");

        //populating the series with data:
        for (int i = 0; i <= finalizedTime; i++){

            if (actualWPM.size() > i)
                actual.getData().add(new XYChart.Data<>(i, actualWPM.get(i)));

            if (averageWPM.size() > i)
                average.getData().add(new XYChart.Data<>(i, averageWPM.get(i)));

        }

        chart.getData().add(actual);
        chart.getData().add(average);

        this.setCenter(chart);

        //playing animation:
        animation.play();

    }

    @Override
    public void rescale(double oldWidth, double newWidth, double oldHeight, double newHeight, boolean horRescale){

        fontSize = Rescale.Font(oldWidth, newWidth,oldHeight, newHeight, horRescale, fontSize);

        for (int i = 0; i < testDataSpace.getChildren().size(); i++){

            ((Label)((VBox)(testDataSpace.getChildren().get(i))).getChildren().get(0)).setFont( new Font(fontSize) );
            ((Label)((VBox)(testDataSpace.getChildren().get(i))).getChildren().get(1)).setFont( new Font(fontSize * 3) );

        }

        ((Label)messageSpace.getChildren().get(0)).setFont( new Font(fontSize) );

    }

    public void setMessageLabelText(String text){

        messageLabel.setText(text);

    }

}
