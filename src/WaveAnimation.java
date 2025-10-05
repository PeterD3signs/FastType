import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class WaveAnimation {

    private final Animation animation;

    public WaveAnimation(Label label){

        animation = new Transition() {

            double i = 0;
            double j = -1;
            boolean cont = true;

            {
                setCycleDuration(Duration.seconds(2));
            }

            @Override
            protected void interpolate(double v) {

                label.setTranslateY(i);

                if (cont) {
                    i = i + j;
                    j = j + 0.03;
                }

                if (i > 0){
                    i = 0;
                    j = -1;
                    cont = false;
                }

            }

        };

    }

    public void playAnimation(){

        animation.play();

    }

}
