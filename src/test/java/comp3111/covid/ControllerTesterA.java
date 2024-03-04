package comp3111.covid;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.junit.After; // added for memory error fix
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.layout.*;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class ControllerTesterA extends ApplicationTest {
    Controller controller;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        stage.setScene(new Scene(loader.load()));
        controller = loader.getController();
        stage.show();
    }
    
    // Task A1
    @Test
    public void A1NoDateAndCountry() {
        clickOn("#tabReport1");
        clickOn("#A1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void A1NoCountry() {
        clickOn("#tabReport1");
        clickOn("#A1Datepicker").write("1/11/2021\n");
        clickOn("#A1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noCountryErrorMsg));
    }
    @Test
    public void A1Normal() {
        clickOn("#tabReport1");
        clickOn("#A1Datepicker").write("1/11/2021\n");
        clickOn("#A1CheckList");
        type(KeyCode.HOME);
        type(KeyCode.SPACE);
        clickOn("#A1SearchButton");
        Long totalCases = controller.A1DisplayTotal.getCellData(0);
        assertEquals(142414L, (long) totalCases);
    }
    @Test
    public void A1InvalidDate() {
        clickOn("#tabReport1");
        clickOn("#A1Datepicker").write("1/11/1999\n");
        clickOn("#A1CheckList");
        type(KeyCode.SPACE);
        clickOn("#A1SearchButton");
        String text = controller.textAreaConsole.getText();
        assertTrue(controller.textAreaConsole.getText().contains(controller.laterDateErrorMsg));
    }
    
    
    
    // Task A2
    @Test
    public void A2NoDateAndCountry() {
        clickOn("#tabApp1");
        clickOn("#A2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void A2NoCountry() {
        clickOn("#tabApp1");
        clickOn("#A2StartDate").write("1/11/2020\n");
        clickOn("#A2EndDate").write("1/11/2021\n");
        clickOn("#A2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noCountryErrorMsg));
    }
    @Test
    public void A2Normal() {
        clickOn("#tabApp1");
        clickOn("#A2StartDate").write("1/11/2020\n");
        clickOn("#A2EndDate").write("1/11/2021\n");
        clickOn("#A2CheckList");
        type(KeyCode.SPACE);
        clickOn("#A2SearchButton");
    }
    @Test
    public void A2TooEarlyDate() {
        clickOn("#tabApp1");
        clickOn("#A2StartDate").write("1/11/1000\n");
        clickOn("#A2EndDate").write("1/11/2021\n");
        clickOn("#A2CheckList");
        type(KeyCode.SPACE);
        clickOn("#A2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.laterDateErrorMsg));
    }
    @Test
    public void A2DiffNegative() {
        clickOn("#tabApp1");
        clickOn("#A2EndDate").write("1/11/2020\n");
        clickOn("#A2StartDate").write("1/11/2021\n");
        clickOn("#A2CheckList");
        type(KeyCode.SPACE);
        clickOn("#A2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.endDateEarlierErrorMsg));
    }
    // A3 tests
    @Test
    public void A3NoDateTest() {
        controller.A3DatePick.setDisable(false);
        controller.A3TopCountrySlider.setDisable(false);
        controller.A3Generate.setDisable(false);
        clickOn("#tabBonus1");
        clickOn("#A3Generate");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void A3ValidDateTest() {
        // replace the list with the test set
        ArrayList<String> originList = controller.listOfCountry;
        controller.listOfCountry = new ArrayList<>();
        controller.listOfCountry.add("China");
        clickOn("#tabBonus1");
        clickOn("#A3LoadButton");
        clickOn("#A3DatePick").write("1/11/2021\n");
        clickOn("#A3Generate");
        controller.listOfCountry = originList;
        assertTrue(controller.A3DrawingArea.getChildren().size() > 0);
    }
    @Test
    public void A3InvalidDateTest() {
        // replace the list with the test set
        ArrayList<String> originList = controller.listOfCountry;
        controller.listOfCountry = new ArrayList<>();
        controller.listOfCountry.add("China");
        clickOn("#tabBonus1");
        clickOn("#A3LoadButton");
        clickOn("#A3DatePick").write("1/11/1000\n");
        clickOn("#A3Generate");
        controller.listOfCountry = originList;
        assertTrue(controller.textAreaConsole.getText().contains(controller.laterDateErrorMsg));
    }
    
    // tear down to remove out of heap memory error
    @After
    public void tearDown() throws Exception{
    	System.out.println("Tearing down instance of ControllerTesterA\n");
    	controller = null;
    	assertNull(controller);
    }
}