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
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.layout.*;
import org.junit.After; // added for memory error fix

import java.util.ArrayList;

import static org.junit.Assert.*;


public class ControllerTesterC extends ApplicationTest {
    Controller controller;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        stage.setScene(new Scene(loader.load()));
        controller = loader.getController();
        stage.show();
    }

    // Task C1
    @Test
    public void C1NoDateAndCountry() {
        clickOn("#tabReport3");
        clickOn("#C1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void C1NoCountry() {
        clickOn("#tabReport3");
        clickOn("#C1Datepicker").write("1/11/2021\n");
        clickOn("#C1SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noCountryErrorMsg));
    }
    @Test
    public void C1Normal() {
        clickOn("#tabReport3");
        clickOn("#C1Datepicker").write("1/11/2021\n");
        clickOn("#C1CheckList");
        type(KeyCode.HOME);
        type(KeyCode.SPACE);
        clickOn("#C1SearchButton");
        String fullyVacNum = controller.C1DisplayVacNum.getCellData(0);
        assertEquals("219,159", fullyVacNum);
    }
    @Test
    public void C1InvalidDate() {
        clickOn("#tabReport3");
        clickOn("#C1Datepicker").write("1/11/1999\n");
        clickOn("#C1CheckList");
        type(KeyCode.SPACE);
        clickOn("#C1SearchButton");
        String text = controller.textAreaConsole.getText();
        assertTrue(controller.textAreaConsole.getText().contains(controller.laterDateErrorMsg));
    }

    // Task C2
    @Test
    public void C2NoDateAndCountry() {
        clickOn("#tabApp3");
        clickOn("#C2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.emptyDateErrorMsg));
    }
    @Test
    public void C2NoCountry() {
        clickOn("#tabApp3");
        clickOn("#C2StartDate").write("1/11/2020\n");
        clickOn("#C2EndDate").write("1/11/2021\n");
        clickOn("#C2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.noCountryErrorMsg));
    }
    @Test
    public void C2Normal() {
        clickOn("#tabApp3");
        clickOn("#C2StartDate").write("1/11/2020\n");
        clickOn("#C2EndDate").write("1/11/2021\n");
        clickOn("#C2CheckList");
        type(KeyCode.SPACE);
        clickOn("#C2SearchButton");
    }
    @Test
    public void C2TooEarlyDate() {
        clickOn("#tabApp3");
        clickOn("#C2StartDate").write("1/11/1000\n");
        clickOn("#C2EndDate").write("1/11/2021\n");
        clickOn("#C2CheckList");
        type(KeyCode.SPACE);
        clickOn("#C2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.laterDateErrorMsg));
    }
    @Test
    public void C2DiffNegative() {
        clickOn("#tabApp3");
        clickOn("#C2EndDate").write("1/11/2020\n");
        clickOn("#C2StartDate").write("1/11/2021\n");
        clickOn("#C2CheckList");
        type(KeyCode.SPACE);
        clickOn("#C2SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains(controller.endDateEarlierErrorMsg));
    }

    //Task C3
    @Test
    public void C3Normal() {
        clickOn("#tabBonus3");
        clickOn("#C3WorldMapPane"); // fixed missing pound sign
    }
    @Test
    public void C3SelectDate() {
        clickOn("#tabBonus3");
        clickOn("#C3Datepicker").write("01/06/2021\n");
        clickOn("#C3SearchButton");
        assertTrue(controller.textAreaConsole.getText().contains("Successfully generated visualization map on Jun 01, 2021"));
    }
    // tear down to remove out of heap memory error
    @After
    public void tearDown() throws Exception {
    	System.out.println("Tearing down instance of ControllerTesterC\n");
    	controller = null;
    	assertNull(controller);
    }
}