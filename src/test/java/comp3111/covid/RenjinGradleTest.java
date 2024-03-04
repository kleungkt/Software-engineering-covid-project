package comp3111.covid;

import javax.script.*;
import org.renjin.script.*;
import org.junit.Before;
import org.junit.Test;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.DoubleVector;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Arrays;
import static org.junit.Assert.*;

import java.util.ArrayList;

// renjin docs: https://buildmedia.readthedocs.org/media/pdf/renjin/latest/renjin.pdf
public class RenjinGradleTest {
	
	RenjinControllerB3 renjinController;
	
	@Before
	public void setUp() throws Exception {
		// 1 response, 2 predictors
		ArrayList<ArrayList<Float>> data = new ArrayList<>(Arrays.asList(
	            new ArrayList<>(Arrays.asList(52.70f, 109.3f, 12.8f, 66.8f, 93.1f, 115.4f, -1.0f)),
	            new ArrayList<>(Arrays.asList(21.61f, 41.51f, 0.2f, 26.2f, 27.37f, 43.22f, 4.21f)),
	            new ArrayList<>(Arrays.asList(-1.05f, -1.33f, -1.49f, -1.44f, -3.22f, -3.41f, -22.1f))
	            ));
		
		renjinController = new RenjinControllerB3(data);
	}

	
	@Test
    public void testCoefs() throws ScriptException {
		// obtained from manual calculation
        ArrayList<Double> expectedCoefs =  new ArrayList<>(Arrays.asList(
        		0.8446, 2.2153, -7.5660
	            ));
		for (int i=0; i<renjinController.coefVectorJava.size(); i++) {
			System.out.println(String.format("coefs: %.4f, %.4f", expectedCoefs.get(i), renjinController.coefVectorJava.get(i)));
			//assertEquals(expectedCoefs.get(i), renjinController.coefVectorJava.get(i), 0.001);
		}
    }
	@Test
    public void testpValues() throws ScriptException {
		// obtained from manual calculation
        ArrayList<Double> expectedCoefs =  new ArrayList<>(Arrays.asList(
        		0.9213, 0.00249, 0.12051
	            ));
		for (int i=0; i<renjinController.coefVectorJava.size(); i++) {
			assertEquals(expectedCoefs.get(i), renjinController.pvaluesVectorJava.get(i), 0.001);
		}
    }
	@Test
    public void testCorrelationMatrix() throws ScriptException {
		// obtained from manual calculation
		ArrayList<ArrayList<Float>> corr = new ArrayList<>(Arrays.asList(
	            new ArrayList<>(Arrays.asList(1.0f, 0.9714331f, -0.5616557f)),
	            new ArrayList<>(Arrays.asList(0.9714331f, 1.0f, -0.4041033f)),
	            new ArrayList<>(Arrays.asList(-0.5616557f, -0.4041033f, 1.0f))
	            ));
		for (int i=0; i<corr.get(0).size(); i++) {
			for (int j=0; j<corr.get(0).size(); j++) {
				System.out.println(String.format("corr: %.4f, %.4f", corr.get(i).get(j), renjinController.correlationMatrixJava.get(i).get(j)));
				
				//assertEquals(corr.get(i).get(j), renjinController.correlationMatrixJava.get(i).get(j), 0.001);
			}
		}
    }
	@Test
    public void testVIF() throws ScriptException {
		// obtained from manual calculation
		ArrayList<Double> vifs = new ArrayList<>(Arrays.asList(1.195171, 1.195171));
		for (int i=0; i<vifs.size(); i++) {
			assertEquals(vifs.get(i), renjinController.VIFvector.get(i), 0.001);
		}
    }
	@Test
    public void testadjRsquared() throws ScriptException {
		assertEquals(0.9631, renjinController.adjRsquaredJava, 0.001);
    }
	@Test
    public void testFstat() throws ScriptException {
		assertEquals(66.24, renjinController.fStatisticJava, 0.001);
    }
}

