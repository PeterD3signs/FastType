import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WordHBox extends HBox {

    private final Label[] letters;

    public WordHBox( String word ){

        letters = new Label[ word.length() + 1];

        for (int i = 0; i < letters.length - 1; i++) {
            letters[i] = new Label(word.charAt(i) + "");
            letters[i].setTextFill(Color.GRAY);
        }

        letters[letters.length - 1] = new Label("  ");

        for (Label letter : letters) this.getChildren().add(letter);

    }

    public void rescale(Font font){

        for (Label letter : letters) letter.setFont(font);

    }

}
