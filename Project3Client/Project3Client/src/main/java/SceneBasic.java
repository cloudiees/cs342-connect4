import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class SceneBasic {
    public BorderPane root;
    public LinkedDS globalObjs;
    Region generateHSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public abstract void updateSceneImgs(boolean dark);

    static Rectangle generateRectangleSpacer(int width, int height) {
        Rectangle rect = new Rectangle(width, height);
        rect.getStyleClass().add("invisSpacer");
        return rect;
    }

    ImageView generateImageView(String imagePath, int width, int height) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    Region generateVSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
