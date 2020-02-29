/**
 Ionut Alixandroae
 @twiter: https://twitter.com/ialixandroae
 @github: https://github.com/ialixandroae
 @page: https://ialixandroae.github.io/
 29 February 2020
 */

package com.ionut.app;

import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URI;

import com.esri.arcgisruntime.data.*;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.kml.*;
import com.esri.arcgisruntime.symbology.*;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class App extends Application {

    private MapView mapView;
    private ArcGISMap map = new ArcGISMap(SpatialReferences.getWebMercator());
    private FeatureLayer uploadedAOI;
    private GraphicsOverlay graphicsOverlay;
    private Graphic kmzGeometry;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        BorderPane mainpane = new BorderPane();
        StackPane left = new StackPane();
        StackPane center = new StackPane();
        Button addServiceBtn = new Button();
        Button applyBuffer = new Button();
        Button downloadZip = new Button();

        TextField bufferValue = new TextField();
        bufferValue.setMaxWidth(270);
        bufferValue.setText("100");

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter zipFilter = new FileChooser.ExtensionFilter("ZIP files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(zipFilter);
        fileChooser.setTitle("Open Resource File");

        left.setPrefWidth(400);
        center.setPrefWidth(800);
        addServiceBtn.setText("Add Archived Shapefile...");
        applyBuffer.setText("Apply Buffer...");
        downloadZip.setText("Download As KMZ...");

        mainpane.setLeft(left);
        mainpane.setCenter(center);

        Scene scene = new Scene(mainpane);
        stage.setTitle("Upload Shapefile - Buffer - Download KMZ");
        stage.setHeight(600);
        stage.setScene(scene);
//        stage.setMaximized(true);
//        stage.setFullScreen(true);
        stage.show();

        mapView = new MapView();
        // DropDown list with basemaps and event to change basemap when clicked
        ComboBox comboBox = new ComboBox<>();
        comboBox.getItems().addAll(FXCollections.observableArrayList(Basemap.Type.values()));

        // Change basemap event
        comboBox.getSelectionModel().selectedItemProperty().addListener(o -> {
            String basemapString = comboBox.getSelectionModel().getSelectedItem().toString().replace(" ","_");
            Basemap _basemap;
            switch (basemapString) {
                case "IMAGERY" :
                    _basemap = Basemap.createImagery();
                    map.setBasemap(_basemap);
                    break;
                case "DARK_GRAY_CANVAS_VECTOR" :
                    _basemap = Basemap.createDarkGrayCanvasVector();
                    map.setBasemap(_basemap);
                    break;
                case "IMAGERY_WITH_LABELS_VECTOR" :
                    _basemap = Basemap.createImageryWithLabelsVector();
                    map.setBasemap(_basemap);
                    break;
                case "LIGHT_GRAY_CANVAS" :
                    _basemap = Basemap.createLightGrayCanvas();
                    map.setBasemap(_basemap);
                    break;
                case "LIGHT_GRAY_CANVAS_VECTOR" :
                    _basemap = Basemap.createLightGrayCanvasVector();
                    map.setBasemap(_basemap);
                    break;
                case "NATIONAL_GEOGRAPHIC" :
                    _basemap = Basemap.createNationalGeographic();
                    map.setBasemap(_basemap);
                    break;
                case "NAVIGATION_VECTOR" :
                    _basemap = Basemap.createNavigationVector();
                    map.setBasemap(_basemap);
                    break;
                case "OCEANS" :
                    _basemap = Basemap.createOceans();
                    map.setBasemap(_basemap);
                    break;
                case "OPEN_STREET_MAP" :
                    _basemap = Basemap.createOpenStreetMap();
                    map.setBasemap(_basemap);
                    break;
                case "STREETS" :
                    _basemap = Basemap.createStreets();
                    map.setBasemap(_basemap);
                    break;
                case "STREETS_NIGHT_VECTOR" :
                    _basemap = Basemap.createStreetsNightVector();
                    map.setBasemap(_basemap);
                    break;
                case "STREETS_WITH_RELIEF_VECTOR" :
                    _basemap = Basemap.createStreetsWithReliefVector();
                    map.setBasemap(_basemap);
                    break;
                case "STREETS_VECTOR" :
                    _basemap = Basemap.createStreetsVector();
                    map.setBasemap(_basemap);
                    break;
                case "TOPOGRAPHIC" :
                    _basemap = Basemap.createTopographic();
                    map.setBasemap(_basemap);
                    break;
                case "TERRAIN_WITH_LABELS" :
                    _basemap = Basemap.createTerrainWithLabels();
                    map.setBasemap(_basemap);
                    break;
                case "TERRAIN_WITH_LABELS_VECTOR" :
                    _basemap = Basemap.createTerrainWithLabelsVector();
                    map.setBasemap(_basemap);
                    break;
                case "TOPOGRAPHIC_VECTOR" :
                    _basemap = Basemap.createTopographicVector();
                    map.setBasemap(_basemap);
                    break;
                default:
                    _basemap = Basemap.createImageryWithLabels();
                    map.setBasemap(_basemap);
            }

            mapView.setMap(map);
        });

        // Set first basemap
        comboBox.getSelectionModel().select(2);

        // create a graphics overlay to contain the buffered geometry graphics
        graphicsOverlay= new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        // set up units to convert from miles to meters
        final LinearUnit meters = new LinearUnit(LinearUnitId.METERS);

        // create a semi-transparent purple fill symbol for the geodesic buffers

        final SimpleFillSymbol uploadedAOISymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x808080FF, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF424FC5, 3));
        final SimpleFillSymbol geodesicFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x80C7EF63, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF8EB72D, 5));

        //  AddServiceBtn event
        addServiceBtn.setOnMouseClicked((event)->{
//            System.out.println(urlText.getText());
            File file = fileChooser.showOpenDialog(stage);
            String filePath = file.getAbsolutePath();
            String destDir = file.getParent() + file.getName().split("\\.")[0];
            File unzippedFolder = new File(destDir);

            unzip(filePath, destDir);
            for (final File fileEntry : unzippedFolder.listFiles()) {
                if (fileEntry.getName().endsWith("shp")) {
                    ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(fileEntry.getAbsolutePath());
                    // use the shapefile feature table to create a feature layer
                    uploadedAOI = new FeatureLayer(shapefileFeatureTable);
                    uploadedAOI.setRenderer(new SimpleRenderer(uploadedAOISymbol));
                    uploadedAOI.addDoneLoadingListener(() -> {
                        if (uploadedAOI.getLoadStatus() == LoadStatus.LOADED) {
                            // zoom to the area containing the layer's features
                            mapView.setViewpointGeometryAsync(uploadedAOI.getFullExtent());
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, uploadedAOI.getLoadError().getMessage());
                            alert.show();
                        };
                    });
                    //  add the feature layer to the map
                    map.getOperationalLayers().add(uploadedAOI);
                }
            };
        });

        applyBuffer.setOnMouseClicked((event)->{
            graphicsOverlay.getGraphics().clear();
            QueryParameters query = new QueryParameters();
            query.setWhereClause("1=1");
            FeatureTable uploadedAOIFeatureTable = uploadedAOI.getFeatureTable();
            ListenableFuture<FeatureQueryResult> tableQueryResult = uploadedAOIFeatureTable.queryFeaturesAsync(query);
            tableQueryResult.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = tableQueryResult.get();
                    Iterator<Feature> features = result.iterator();
                    while(features.hasNext()) {
                        Feature _feature = features.next();
                        Polygon bufferedAOI = GeometryEngine.bufferGeodetic(_feature.getGeometry(), Double.parseDouble(bufferValue.getText()), meters, Double.NaN, GeodeticCurveType.GEODESIC);
                        Graphic geodesicBufferGraphic = new Graphic(bufferedAOI, geodesicFillSymbol);
                        kmzGeometry = geodesicBufferGraphic;
                        graphicsOverlay.getGraphics().addAll(Arrays.asList(geodesicBufferGraphic));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        //  Compute buffer and download Shapefile as .zip
        downloadZip.setOnMouseClicked((event)->{
            // create a KML layer from a blank KML document and add it to the map
            KmlDocument kmlDocument = new KmlDocument();
            KmlDataset kmlDataset = new KmlDataset(kmlDocument);
            KmlLayer kmlLayer = new KmlLayer(kmlDataset);
            KmlGeometry kmlGeometry = new KmlGeometry(kmzGeometry.getGeometry(), KmlAltitudeMode.CLAMP_TO_GROUND);
            KmlPlacemark currentKmlPlacemark = new KmlPlacemark(kmlGeometry);
            // update the style of the current KML placemark
            KmlStyle kmlStyle = new KmlStyle();
            currentKmlPlacemark.setStyle(kmlStyle);
            KmlPolygonStyle kmlPolygonStyle = new KmlPolygonStyle(ColorUtil.colorToArgb(Color.CORAL));
            kmlPolygonStyle.setFilled(true);
            kmlPolygonStyle.setOutlined(false);
            kmlStyle.setPolygonStyle(kmlPolygonStyle);
            // add the placemark to the kml document
            kmlDocument.getChildNodes().add(currentKmlPlacemark);

            // get a path from the file chooser
            FileChooser kmzFileChooser = new FileChooser();
            FileChooser.ExtensionFilter kmzFilter = new FileChooser.ExtensionFilter("KMZ files (*.kmz)", "*.kmz");
            kmzFileChooser.getExtensionFilters().add(kmzFilter);
            File kmzFile = kmzFileChooser.showSaveDialog(mapView.getScene().getWindow());
            if (kmzFile != null) {
                // save the KML document to the file
                kmlDocument.saveAsAsync(kmzFile.getPath()).addDoneListener(() ->
                        new Alert(Alert.AlertType.INFORMATION, "KMZ file saved.").show()
                );
            }
        });

        // Add other elements in LeftPane
        // Add the map view to stack pane
        Label urlLabel = new Label("Import Shapefile from a .zip file");
        Label basemapText = new Label("Select a basemap from the list to change the map");

        Label bufferText = new Label("Select buffer value (in meters)");
        Label downloadZipText = new Label("Download buffered shapefile");

        Hyperlink  author = new Hyperlink ();
        author.setText("Made by Ionut Alixandroae");
        author.setOnMouseClicked((evt) -> {
            openUrl("https://twitter.com/ialixandroae");
            author.setVisited(false);
        });
        Hyperlink  javaRuntime = new Hyperlink ();
        javaRuntime.setText("ArcGIS Runtime SDK for Java");
        javaRuntime.setOnMouseClicked((evt) -> {
            openUrl("https://developers.arcgis.com/java/latest/");
            javaRuntime.setVisited(false);
        });

        center.getChildren().addAll(mapView);
        left.getChildren().addAll(urlLabel, addServiceBtn, bufferText, bufferValue, applyBuffer, downloadZip, downloadZipText, basemapText, comboBox, author, javaRuntime);

        left.setAlignment(urlLabel,Pos.TOP_LEFT);
        left.setMargin(urlLabel, new Insets(10, 0, 0, 10));

        left.setAlignment(addServiceBtn,Pos.TOP_LEFT);
        left.setMargin(addServiceBtn, new Insets(30, 0, 0, 10));

        left.setAlignment(bufferText,Pos.TOP_LEFT);
        left.setMargin(bufferText, new Insets(60, 0, 0, 10));

        left.setAlignment(bufferValue,Pos.TOP_LEFT);
        left.setMargin(bufferValue, new Insets(80, 0, 0, 10));

        left.setAlignment(applyBuffer,Pos.TOP_LEFT);
        left.setMargin(applyBuffer, new Insets(110, 0, 0, 10));

        left.setAlignment(downloadZipText,Pos.TOP_LEFT);
        left.setMargin(downloadZipText, new Insets(140, 0, 0, 10));

        left.setAlignment(downloadZip,Pos.TOP_LEFT);
        left.setMargin(downloadZip, new Insets(160, 0, 0, 10));

        left.setAlignment(basemapText,Pos.TOP_LEFT);
        left.setMargin(basemapText, new Insets(190, 0, 0, 10));

        left.setAlignment(comboBox, Pos.TOP_LEFT);
        left.setMargin(comboBox, new Insets(210, 0, 0, 10));

        left.setAlignment(author,Pos.BOTTOM_RIGHT);
        left.setMargin(author,new Insets(0,10,30,0));

        left.setAlignment(javaRuntime,Pos.BOTTOM_RIGHT);
        left.setMargin(javaRuntime,new Insets(430,10,0,0));
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }

    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
//                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openUrl(String url){
        Desktop desktop = Desktop.getDesktop();
        try{
            desktop.browse(new URI(url));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
