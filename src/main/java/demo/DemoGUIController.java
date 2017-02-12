package demo;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import sensor.SensorInterface;
import sensor.SensorSimulation;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.KeyFrame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jholland on 2/11/17.
 */
public class DemoGUIController implements Initializable {
  @FXML
  public Slider zoomSlider;
  @FXML
  public ImageView imageView;

  private WritableImage image;

  private BufferedImage buildable_image = new BufferedImage(4000, 4000, BufferedImage.TYPE_INT_ARGB);

  @FXML
  public ToggleButton turnOnCam;

  @FXML
  public Label statusLabel; //Whether cam is on or off

  @FXML
  public Label imageStatus; //Whether image is available or not

  @FXML
  public Button takePicture;
  @FXML
  public Button reset;

  private SensorInterface sensor;

  private boolean previousCaptureStatus;
  private boolean loadimage;
  private int i, j;


    @Override
  public void initialize(URL location, ResourceBundle resources) {

    sensor = new SensorSimulation();

    Timeline updateClock = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        updateCamStatusLabel();
        updateImageStatus();
        updateImage();
      }
    }));
    updateClock.setCycleCount(Timeline.INDEFINITE);
    updateClock.play();




    //Keep the slider as integer
    zoomSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        zoomSlider.setValue(newValue.intValue()));

    takePicture.setOnAction(event -> takePicture());
    reset.setOnAction(event -> reset());
    turnOnCam.setOnAction(event -> toggleOnOff());

  }

  private void toggleOnOff() {
    System.out.println("Toggled");
    if (this.turnOnCam.isSelected()) {
        sensor.on();
    } else {
        sensor.off();
    }
  }

  //Temporary update status for camera state
  private void updateCamStatusLabel(){
      if(sensor.status()) statusLabel.setText("Status: On");
      else statusLabel.setText("Status: Off");
  }

  private void updateImageStatus(){
    if(sensor.captureStatus()) imageStatus.setText("Image: READY");
    else imageStatus.setText("Image: NOT READY");
  }

  private void updateImage(){
      boolean status = sensor.captureStatus();
      if(status != previousCaptureStatus) {
          loadimage = true;
          i = 0;
          j = 0;
      }
      previousCaptureStatus = status;

      if(loadimage){
          BufferedImage chunk = sensor.getImageChunk(i * 100 + 50, j * 100 + 50, 100);

          if (chunk != null) {
              Graphics2D g = buildable_image.createGraphics();
              g.drawImage(chunk, i * 100, j * 100, 100, 100, null);
              imageView.setImage(SwingFXUtils.toFXImage(buildable_image, image));

              i++;
              if (i >= 40) {
                  i = 0;
                  j++;
                  if (j >= 40) loadimage = false;
              }
          }
      }
  }

  private void reset() {
    System.out.println("Reset Camera ON/OFF");
    sensor.reset();
  }

  private void takePicture() {
    sensor.takePicture((int) zoomSlider.getMin());
  }


}
