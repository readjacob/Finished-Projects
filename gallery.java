package cs1302.gallery;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Gallery App for UGA course CS1302 Project 3
 *
 * Opens a window, displays the artwork for itunes search api results for a given query,
 * allows users to input their own custom search keywords, rotates a new image from the
 * collected pool every two seconds.
 *
 * @author Jacob Read
 */
public class GalleryApp extends Application {

    ArrayList<ImageView> currentRotation = new ArrayList<ImageView>(); //Array containing all results from the itunes api
    ArrayList<ImageView> inUse = new ArrayList<ImageView>(); //Array containing those results that are currently being displayed
    ArrayList<ImageView> notInUse = new ArrayList<ImageView>(); //Array containing those results that are not currently being displayed.


   /**
   * Updates the app's display with images from a given list of URLs.
   * @param list a list of URLs that link to the artwork for the music resulted from the itunes search
   * @param mainPane a reference to the GUI's root node so that its children elements can be accessed.
   * @throws IOException if the URL.openStream() method does not work correctly.
   */
    private void updateDisplay(ArrayList<URL> list, VBox mainPane) {
            FlowPane newDisplay = new FlowPane();
            newDisplay.setPrefWrapLength(512);
            currentRotation = new ArrayList<ImageView>();
            inUse = new ArrayList<ImageView>();
            notInUse = new ArrayList<ImageView>();

            for(URL art : list) {
                    Image image = null;
                    try {
                            image = new Image(art.openStream());
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
                    currentRotation.add(new ImageView(image));

                    mainPane.getChildren().set(2, newDisplay);
            }

            for(int k = 0; k < currentRotation.size(); k++) {
                    if(k < 20) {
                            inUse.add(currentRotation.get(k));
                            newDisplay.getChildren().add(currentRotation.get(k));
                            ((HBox)mainPane.getChildren().get(3)).getChildren().set(1, new ProgressBar((k+1)*5));
                        }
                        else {
                                notInUse.add(currentRotation.get(k));
                        }
                }
        }

        /**
        * Grabs the keywords that the user has input into the App's text box.
        *
        * @param query a reference to the App GUI's textbox where the user inputs the search keywords that they want to
        *               send to the itunes API.
        *
        */
        private String getQueryURL(TextField query) {
                String url;

                String search = query.getCharacters().toString().trim();
                ArrayList<String> searchTerms = new ArrayList<String>();

                boolean termsLeft = true;

                while(termsLeft == true) {
                    if(search.indexOf(' ') == -1) {
                            searchTerms.add(search);
                            termsLeft = false;
                    }
                    else {
                            searchTerms.add(search.substring(0, search.indexOf(' ')));
                            search = search.substring(search.indexOf(' ') + 1);
                    }
            }

            url = "https://itunes.apple.com/search?term=";

            for(int i = 0; i < searchTerms.size(); i++) {
                    if(i > 0) {
                            url += "+";
                    }
                    url += searchTerms.get(i);
            }

            url += "&media=music";

            return url;
    }

    /**
    * Takes a string and it turns into a URL object that references the JSON returned by the itunes search
    * API in response to a query.
    * @param str a string representing the URL of the JSON returned by the itunes search API
    */
    private URL getJSON(String str) {
            URL Json = null;
            try {
                    Json = new URL(str);
            } catch (MalformedURLException e) {
                    e.printStackTrace();
            }

            return Json;
    }

        /**
        * Finds the URLs referencing the music's artwork in a JSON file, puts them in an ArrayList, and returns that
        * list.
        * @param json a URL object that references the JSON file returned by the itunes search API.
        */
        private ArrayList<URL> parseJSONforArtworkURLs(URL json) {
            ArrayList<URL> artworkUrlList = new ArrayList<URL>();
            ArrayList<String> artworkStringList = new ArrayList<String>();

            Reader reader = null;

            try {
                    reader = new InputStreamReader(json.openStream());
            } catch (IOException e1) {
                    e1.printStackTrace();
            }
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(reader);
            JsonObject root = je.getAsJsonObject();
            JsonArray results = root.getAsJsonArray("results");
            int numResults = results.size();
            JsonObject result = results.get(i).getAsJsonObject();
            JsonElement artworkUrl100 = result.get("artworkUrl100");
            if (artworkUrl100 != null) {
                 String artUrl = artworkUrl100.getAsString();

                 if(!artworkStringList.contains(artUrl)) {
                         artworkStringList.add(artUrl);

                        try {
                                artworkUrlList.add(new URL(artUrl));
                        } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                }
                }
            }
    }

        return artworkUrlList;
}

/**
* Brings all of the programs helper methods together, occurs when the Update Images button is pressed and
        * does all the work of checked the search query, searching it, compiling, and displaying the results.
        *
        * @param query a reference to the app GUI's text field that holds the search terms.
        * @param pane a reference to the app GUI's root node that can be used to access all of its children nodes.
        */
        public void updateImages(TextField query, VBox pane) {
            ArrayList<URL> newImages = parseJSONforArtworkURLs(getJSON(getQueryURL(query)));
            ((HBox)pane.getChildren().get(3)).getChildren().set(1, new ProgressBar(0));

            if(newImages.size() < 20) {
                    pane.getChildren().set(2, new Label("Search term returned less than 20 results. Please try new term."));
            }
            else {
                    updateDisplay(newImages, pane);
            }
    }

        /**
        * Sets the App's GUI to its default "light" setting.
        *
        * @param root a reference to the app GUI's root node that can be used to access all of its children nodes.
        */
        private void lightTheme(VBox root) {
            String style = "-fx-background-color: skyblue;";
            String HBoxStyle = style + "-fx-alignment: center; ";
            String buttonStyle = "-fx-background-color: palegreen;";        


            root.setStyle(style);
            root.getChildren().get(1).setStyle(HBoxStyle);
            ((HBox)root.getChildren().get(1)).getChildren().get(0).setStyle(buttonStyle);
            ((HBox)root.getChildren().get(1)).getChildren().get(2).setStyle(buttonStyle);
            ((HBox)root.getChildren().get(1)).getChildren().get(3).setStyle(buttonStyle);
    }

        /**
        * Sets the App;s GUI to its "dark" setting.
        *
        * @param root a reference to the app GUI's root node that can be used to access all of its children nodes.
        */
        private void darkTheme(VBox root) {
            String style = "-fx-background-color: darkgray;";
            String HBoxStyle = style + "-fx-alignment: center; ";
            String buttonStyle = "-fx-background-color: khaki;";

            root.setStyle(style);
            root.getChildren().get(1).setStyle(HBoxStyle);
            ((HBox)root.getChildren().get(1)).getChildren().get(0).setStyle(buttonStyle);
            ((HBox)root.getChildren().get(1)).getChildren().get(2).setStyle(buttonStyle);
            ((HBox)root.getChildren().get(1)).getChildren().get(3).setStyle(buttonStyle);
    }

@Override
public void start(Stage stage) {

    VBox pane = new VBox(0);
    Scene scene = new Scene(pane);

    KeyFrame keyframe = new KeyFrame(Duration.seconds(2), e -> {

            int indexRemoved = (int)(Math.random() * 20);

            ImageView inserted = notInUse.remove((int)(Math.random() * (notInUse.size())));
            ImageView removed = (ImageView)((FlowPane)pane.getChildren().get(2)).getChildren().set(indexRemoved, inserted);
            notInUse.add(removed);
    });

    Timeline timeline = new Timeline();
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.getKeyFrames().add(keyframe);

    MenuItem exit = new MenuItem("Exit");
    exit.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        Menu file = new Menu("File");
        file.getItems().add(exit);
        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {                            //This EventHandler creates an Application Modal About window.
                Stage stg = new Stage();
                VBox root = new VBox();
                root.setStyle("-fx-alignment: center;");
                Scene scn = new Scene(root);

                Image image = null;

                try {
                                image = new Image(new URL("https://i.imgur.com/RP4h4eM.jpg").openStream());
                        } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                        } catch (IOException e1) {
                                e1.printStackTrace();
                        }

                        ImageView pic = new ImageView(image);
                        pic.setFitHeight(150);
                        pic.setFitWidth(100);
                        Label name = new Label("Jacob Read");
                        Label school = new Label("UGA");
                        Label email = new Label("jar90251@uga.edu");
                        Label version = new Label("version: 1.1.2");
        
                        root.getChildren().addAll(pic, name, school, email, version);
        
                        stg.setScene(scn);
                        stg.setTitle("About Jacob");
                        stg.initModality(Modality.APPLICATION_MODAL);
                        stg.initOwner(stage);
                        stg.show();
                });
                help.getItems().add(about);
                Menu theme = new Menu("Theme");
                MenuItem light = new MenuItem("light");
                light.setOnAction(e -> {
                        lightTheme(pane);                 //sets the GUI's theme to light on action
                    });
                    MenuItem dark = new MenuItem("dark");
                    dark.setOnAction(e -> {
                            darkTheme(pane);                  //sets the GUI's theme to dark on action
                    });
                    theme.getItems().addAll(light, dark);
                    MenuBar menuBar = new MenuBar(file, theme, help);
            
                    FlowPane display = new FlowPane();
            
                    HBox progressBar = new HBox(10);
                    ProgressBar percentComplete = new ProgressBar(100);
                    Label progress = new Label("Progress: ");
                    Label courtesy = new Label("Images Courtesty of iTunes");
                    progressBar.getChildren().addAll(progress, percentComplete, courtesy);
            
                    HBox toolBar = new HBox(10);
                    Button startStop = new Button("Play");
                    startStop.setOnAction(e -> {
                            if(startStop.getText().equals("Play")) {
                                    startStop.setText("Pause");
                                    timeline.play();
                                }
                                else {
                                        startStop.setText("Play");
                                        timeline.pause();
                                }
                        });
                        Label searchQuery = new Label("Search Query:");
                        TextField searchField = new TextField("rock");
                        searchField.setOnAction(e -> {
                                        updateImages(searchField, pane);
                        });
                        Button update = new Button("Update Images");
                        update.setOnAction(e -> {
                                updateImages(searchField, pane);
                        });
                        toolBar.getChildren().addAll(startStop, searchQuery, searchField, update);
                
                        pane.getChildren().addAll(menuBar, toolBar, display, progressBar);
                
                        updateImages(searchField, pane);
                
                
                        stage.setMaxWidth(512);
                        stage.setMinWidth(512);
                        stage.setMaxHeight(500);
                        stage.setMinHeight(500);
                        stage.setTitle("Jacob's Gallery");
                        stage.setScene(scene);
                
                        lightTheme(pane);                      //starts the app with the light thematic setting
                        stage.show();
                    }
                
                    public static void main(String[] args) {
                        try {
                            Application.launch(args);
                        } catch (UnsupportedOperationException e) {
                            System.out.println(e);
                            System.err.println("If this is a DISPLAY problem, then your X server connection");
                            System.err.println("has likely timed out. This can generally be fixed by logging");
                            System.err.println("out and logging back in.");
                            System.exit(1);
                        }
                    }
                
                }                                                                    