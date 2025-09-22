package header;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class Header {
    public Button leftButton;
    public TextField header;
    public HBox hbox;
    public Region spacer, spacer2;
    public Rectangle invisSpacer;
    public Header(String headerTxt) {
        header = new TextField(headerTxt);
        header.setEditable(false);
        header.getStyleClass().add("header");
        header.setPrefColumnCount(headerTxt.length() / 2 + 4);
        spacer = createSpacer();
        spacer2 = createSpacer();
        leftItem();
        rightItemAndCreate();
        paddingAndAlignment();
    }
    public Region createSpacer(){
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    public void leftItem(){
        leftButton = new Button();
    }
    public void rightItemAndCreate(){
        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream("/images/white/back.png")));
        imageView1.setFitHeight(40);
        imageView1.setFitWidth(40);
        leftButton.setGraphic(imageView1);
        invisSpacer = new Rectangle(51, 51);
        invisSpacer.getStyleClass().add("invisSpacer");
        hbox = new HBox(leftButton, spacer, header, spacer2, invisSpacer);
    }
    public void paddingAndAlignment(){
        hbox.setPadding(new Insets(10));
        hbox.setAlignment(Pos.CENTER);
    }
}
