package comp3111.covid;

import comp3111.covid.c3worldmap.World;
import comp3111.covid.c3worldmap.WorldBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Test;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.layout.*;
import org.junit.After; // added for memory error fix

import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class ControllerTesterB extends ApplicationTest {
    Controller controller;
    private World C3GenerateWorld() {
        World C3World = WorldBuilder.create()
                .resolution(World.Resolution.HI_RES)
                .locationIconCode(MaterialDesign.MDI_STAR)
                .zoomEnabled(false)
                .selectionEnabled(true)
                .showLocations(true)
                .prefSize(719,360)
                .maxSize(719,360)
                .layoutY(40)
                .scaleY(0.8)
                .build();
        return C3World;
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        stage.setScene(new Scene(loader.load()));
        controller = loader.getController();
        World C3World = C3GenerateWorld();
        AnchorPane C3Pane = new AnchorPane(C3World);
        controller.C3WorldMapPane.getChildren().clear();
        controller.C3WorldMapPane.getChildren().add(C3Pane);
        stage.show();
    }

   // B1 tests
    @Test
    public void B1InvalidDate() {
        clickOn("#tabReport2");
        clickOn("#B1Datepicker").write("1/32/1999\n");
        clickOn("#B1CheckList");
        type(KeyCode.SPACE);
        clickOn("#B1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void B1InvalidDate2() {
        clickOn("#tabReport2");
        clickOn("#B1Datepicker").write("12/27/1969\n");
        clickOn("#B1CheckList");
        type(KeyCode.SPACE);
        clickOn("#B1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void B1ValidDate() {
        clickOn("#tabReport2");
        clickOn("#B1Datepicker").write("1/12/9999\n");
        clickOn("#B1CheckList");
        type(KeyCode.SPACE);
        clickOn("#B1SearchButton");
        assertFalse(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void B1NoCountry() {
        clickOn("#tabReport2");
        clickOn("#B1Datepicker").write("11/10/2020\n");
        clickOn("#B1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noCountryErrorMsg));
    }
    @Test
    public void B1Normal() {
    	// Afghanistan
        clickOn("#tabReport2");
        clickOn("#B1Datepicker").write("19/07/2021\n");
        clickOn("#B1CheckList");
        type(KeyCode.HOME);
        type(KeyCode.SPACE);
        clickOn("#B1SearchButton");
        Long totalDeaths = controller.B1DisplayTotal.getCellData(0);
        assertEquals(6213L, (long)totalDeaths);
    }
    @Test
    public void B1Normal2() {
    	// Afghanistan
        clickOn("#tabReport2");
        clickOn("#B1Datepicker").write("19/07/2021\n");
        clickOn("#B1CheckList");
        type(KeyCode.HOME);
        type(KeyCode.SPACE);
        clickOn("#B1SearchButton");
        assertEquals(159.601f, Float.valueOf(controller.B1DisplayTotal1M.getCellData(0)), 0.001);
    }



    // Task B2
    @Test
    public void B2NoStartDate() {
        clickOn("#tabApp2");
        clickOn("#B2EndDate").write("25/08/2020\n");
        clickOn("#B2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void B2NoCountry() {
        clickOn("#tabApp2");
        clickOn("#B2StartDate").write("25/08/2020\n");
        clickOn("#B2EndDate").write("10/11/2021\n");
        clickOn("#B2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noCountryErrorMsg));
    }
    @Test
    public void B2Normal() {
    	clickOn("#tabApp2");
        clickOn("#B2StartDate").write("1/11/2020\n");
        clickOn("#B2EndDate").write("1/11/2021\n");
        clickOn("#B2CheckList");
        type(KeyCode.SPACE);
        clickOn("#B2SearchButton");
    }
    public void B2TooEarlyDate() {
        clickOn("#tabApp2");
        clickOn("#B2StartDate").write("1/11/1000\n");
        clickOn("#B2EndDate").write("1/11/10401\n");
        clickOn("#B2CheckList");
        type(KeyCode.SPACE);
        clickOn("#B2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.laterDateErrorMsg));
    }
    @Test
    public void B2DiffNegative() {
        clickOn("#tabApp2");
        clickOn("#B2EndDate").write("1/11/2020\n");
        clickOn("#B2StartDate").write("1/11/2021\n");
        clickOn("#B2CheckList");
        type(KeyCode.SPACE);
        clickOn("#B2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.endDateEarlierErrorMsg));
    }



    // Task B3
    @Test
    public void B3OnePredictorVIF() {
        clickOn("#tabBonus2");
        clickOn("#B3CheckList");
        controller.B3CheckList.getCheckModel().check(0); // check the 0th item in the dropdown aka stringency_index
        clickOn("#B3GenerateReportButton");
        clickOn("#B3MulticollinearityButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.vifNotDefinedErrorMsg));
    }
    @Test
    public void B3NoPredictors() {
        clickOn("#tabBonus2");
        clickOn("#B3CheckList");
        clickOn("#B3GenerateReportButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noPredictorErrorMsg));
    }
    @Test
    public void B3MultiPredictorRegression() {
        clickOn("#tabBonus2");
        clickOn("#B3CheckList");
        //"gdp_per_capita","extreme_poverty","female_smokers","male_smokers"
        controller.B3CheckList.getCheckModel().check(6);
        controller.B3CheckList.getCheckModel().check(7);
        controller.B3CheckList.getCheckModel().check(10);
        controller.B3CheckList.getCheckModel().check(11);
        clickOn("#B3GenerateReportButton");
        clickOn("#B3LinearRegressionOutputButton");
        // b3RegressionPane -> hbox -> B3PredictorPane, B3OutputPane -> {}, {vbox} -> {}, {multRsq, adjRsquared, Fhat, sigma}
        // have to do this because I can't predefine the FXML ids and need to declare this label value at runtime
        Pane pn = ((Pane)((HBox)controller.B3RegressionPane.getChildren().get(0)).getChildren().get(1));
        VBox v = (VBox)pn.getChildren().get(0);
        Label lb = (Label)v.getChildren().get(1);
        assertTrue(lb.getText().contains("0.5195"));
    }
    @Test
    public void B3MultiPredictorCollinearity() {
        clickOn("#tabBonus2");
        clickOn("#B3CheckList");
        //"gdp_per_capita","extreme_poverty","female_smokers","male_smokers"
        controller.B3CheckList.getCheckModel().check(6);
        controller.B3CheckList.getCheckModel().check(7);
        controller.B3CheckList.getCheckModel().check(10);
        controller.B3CheckList.getCheckModel().check(11);
        clickOn("#B3GenerateReportButton");
        clickOn("#B3MulticollinearityButton");

        GridPane pn = (GridPane)controller.B3MulticollinearityPane.getChildren().get(0);
        Label lb = null;
        for (Node node : pn.getChildren()) {
        	if (pn.getColumnIndex(node)==1 && pn.getRowIndex(node)==1) {
        		lb = (Label)node;
        		break;
        	}
        }
        assertTrue(lb.getText().contains("2.020"));
    }
    @Test
    public void B3MultiPredictorCorrelation() {
        clickOn("#tabBonus2");
        clickOn("#B3CheckList");
        //"gdp_per_capita","extreme_poverty","female_smokers","male_smokers"
        controller.B3CheckList.getCheckModel().check(6);
        controller.B3CheckList.getCheckModel().check(7);
        controller.B3CheckList.getCheckModel().check(10);
        controller.B3CheckList.getCheckModel().check(11);
        clickOn("#B3GenerateReportButton");
        clickOn("#B3CorrelationMatrixButton");

        ScrollPane pn = (ScrollPane)controller.B3CorrelationPane.getChildren().get(0);
        GridPane gp = (GridPane)pn.getContent();
        Label lb = null;
        for (Node node : gp.getChildren()) {
        	if (gp.getColumnIndex(node)==4 && gp.getRowIndex(node)==0) {
        		lb = (Label)node;
        		break;
        	}
        }
        assertTrue(lb.getText().contains("0.669"));
    }



    // tear down to remove out of heap memory error
    @After
    public void tearDown() throws Exception {
    	System.out.println("Tearing down instance of ControllerTesterB\n");
    	controller = null;
    	assertNull(controller);
    }
}