import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;

public class Animations {
    public static void fade(List<Node> nodes, Pane parent){
        ParallelTransition parallel = new ParallelTransition();
        for(Node node : nodes){
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setDelay(Duration.millis(500));
            parallel.getChildren().add(fadeTransition);
        }
        parallel.setOnFinished(event -> {
            parent.getChildren().removeAll(nodes);
        });
        parallel.play();
    }

    public static void greenFlash(Node node){
        String originalStyles = node.getStyle();
        node.setStyle("-fx-background-color: #63ffa7;");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(node.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(400), new KeyValue(node.opacityProperty(), .4)),
                new KeyFrame(Duration.millis(800), new KeyValue(node.opacityProperty(), 1)));
        timeline.play();
        timeline.setOnFinished(event -> {
           node.setStyle(originalStyles);
        });
    }
}
