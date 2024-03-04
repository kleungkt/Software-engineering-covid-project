package comp3111.covid;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.script.*;
import org.renjin.script.*;
import org.renjin.sexp.*;

// renjin docs: https://buildmedia.readthedocs.org/media/pdf/renjin/latest/renjin.pdf

/**
 * sets up controller class for evaluating R scripts and computing relevant statistics
 */
public class RenjinControllerB3 {
	
	private SEXP coefVector;
	private SEXP fittedVector;
	private SEXP adjRsquared;
	private SEXP multRsquared;
	private SEXP pvaluesVector;
	private SEXP fStatistic;
	private SEXP sigmaHat; // residual standard error
	private SEXP stdResiduals; // internally studentised residuals
	
	private SEXP prunedDataMatrix; // response variable and all predictors after removing all NAs
	private SEXP naVector; // count number of missing values for response and each predictor
	private SEXP correlationMatrix;
	
	public ArrayList<Double> coefVectorJava = new ArrayList<>();
	public ArrayList<Double> fittedVectorJava = new ArrayList<>();
	public double adjRsquaredJava;
	public double multRsquaredJava;
	public ArrayList<Double> pvaluesVectorJava = new ArrayList<>();
	public double fStatisticJava;
	public int fDF1; // degrees of freedom of F statistic
	public int fDF2;
	public double sigmaHatJava;
	public ArrayList<Double> stdResidualsJava = new ArrayList<>();
	public ArrayList<Integer> naVectorJava = new ArrayList<>();
	public ArrayList<ArrayList<Double>> prunedDataMatrixJava  = new ArrayList<>();	
	public ArrayList<ArrayList<Double>> correlationMatrixJava = new ArrayList<>();
	
	public int insufficientData = 0;
	public int totalMissing = -1;
	public int totalDataPoints = -1;
	
	public ArrayList<Double> VIFvector = new ArrayList<>();

	/**
	 * constructor of RenjinControllerB3
	 * creates a RenjinScriptEngineFactory and ScriptEngine to run R commands, then runs linear regression on predictorMatrix and stores the appropriate outputs in both SEXP and ArrayList forms
	 * @param predictorMatrix - an ArrayList of Float ArrayLists containing the response + predictor values to run regression on
	 * @throws ScriptException - if it fails to parse an R script
	 */
	public RenjinControllerB3(ArrayList<ArrayList<Float>> predictorMatrix) throws ScriptException {
		
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine();
		
		// load the ArrayList<ArrayList<Float>> into df_list. It is now a list
		engine.put("df_list", predictorMatrix);
		
		// load into a proper dataframe by column. Now it has (p+1) columns (1st column = response var, others = predictors) and n rows where n is number of data
		engine.eval("df <- data.frame(matrix(unlist(df_list), ncol=length(df_list), byrow=FALSE))");
		
		// remove missing values (-1) from data
		engine.eval("df[df==-1] <- NA");
		engine.eval("new_df <- na.omit(df)");
		
		// check if we removed all datapoints from regression
		engine.eval("insufficientData <- 0");
		engine.eval("if ((dim(new_df)[1] < 2)==TRUE){insufficientData <- 1}");
		insufficientData = ((DoubleArrayVector)engine.eval("insufficientData")).getElementAsInt(0);
		if (insufficientData==1) {
			return;
		}
		
		// get total number of datapoints remaining in analysis and total number of missing datapoints
		totalDataPoints = ((IntVector)engine.eval("dim(new_df)[1]")).getElementAsInt(0);
		totalMissing = ((IntVector)engine.eval("dim(df)[1]-dim(new_df)[1]")).getElementAsInt(0);
		
		// linear regression
		engine.eval("model = lm(X1 ~ ., data=new_df)");

		// extract relevant values in R analysis
		coefVector = (SEXP)engine.eval("model$coefficients");
		fittedVector = (SEXP)engine.eval("model$fitted.values");
		adjRsquared = (SEXP)engine.eval("summary(model)$adj.r.squared");
		multRsquared = (SEXP)engine.eval("summary(model)$r.squared");
		pvaluesVector = (SEXP)engine.eval("summary(model)$coefficients[,'Pr(>|t|)']");
		fStatistic = (SEXP)engine.eval("summary(model)$fstatistic");
		sigmaHat = (SEXP)engine.eval("sigma(model)"); // need to also extract df, which is n-p-1
		stdResiduals = (SEXP)engine.eval("rstandard(model)");
		correlationMatrix = (SEXP)engine.eval("cor(new_df)");
		
		DoubleVector x = (DoubleVector)coefVector;
		for (int i=0; i<x.length(); i++) {
			coefVectorJava.add(x.getElementAsDouble(i));
		}
		x = (DoubleVector)fittedVector;
		for (int i=0; i<x.length(); i++) {
			fittedVectorJava.add(x.getElementAsDouble(i));
		}
		x = (DoubleVector)pvaluesVector;
		for (int i=0; i<x.length(); i++) {
			pvaluesVectorJava.add(x.getElementAsDouble(i));
		}
		x = (DoubleVector)stdResiduals;
		for (int i=0; i<x.length(); i++) {
			stdResidualsJava.add(x.getElementAsDouble(i));
		}
		
		adjRsquaredJava = ((DoubleVector)adjRsquared).getElementAsDouble(0);
		multRsquaredJava = ((DoubleVector)multRsquared).getElementAsDouble(0);
		fStatisticJava = ((DoubleVector)fStatistic).getElementAsDouble(0);
		fDF1 = ((DoubleVector)fStatistic).getElementAsInt(1);
		fDF2 = ((DoubleVector)fStatistic).getElementAsInt(2);
		sigmaHatJava = ((DoubleVector)sigmaHat).getElementAsDouble(0);
		
		prunedDataMatrix = (SEXP)engine.eval("new_df");
		naVector = (SEXP)engine.eval("is.na(df)"); // print how many missing values for each predictor and response
		
		// prunedDataMatrix must be converted to ListVector then each element of it extracted as a DoubleVector
		//System.out.println("The data vec is: " + ((ListVector)prunedDataMatrix).getElementAsVector("X1"));
		
		DoubleVector y = ((DoubleVector)((ListVector)prunedDataMatrix).getElementAsVector("X1"));
		// if k coefficients, there are k-1 predictors and 1 response (since there is intercept)
		for (int i=0; i<coefVectorJava.size(); i++) {
			ArrayList<Double> temp = new ArrayList<>();
			for (int j=0; j<y.length(); j++) {
				temp.add(y.getElementAsDouble(j));
			}
			prunedDataMatrixJava.add(temp);
		}
		
		// if k coefficients, there are k-1 predictors and 1 response (since there is intercept)
		for (int i=0; i<coefVectorJava.size(); i++) {
			ArrayList<Double> temp = new ArrayList<>();
			for (int j=0; j<coefVectorJava.size(); j++) {
				temp.add(((DoubleVector)correlationMatrix).getElementAsDouble(i*coefVectorJava.size()+j));
			}
			correlationMatrixJava.add(temp);
		}
		
		// extract number of NA values for each column
		for (int i=0; i<coefVectorJava.size(); i++) {
			String command = String.format("colSums(is.na(df))[%d]", i+1);
			DoubleVector z = (DoubleVector)engine.eval(command);
			naVectorJava.add(z.getElementAsInt(0));
		}
		
		///////////////////////////////////
		
		// compute VIF for each variable in the model
		// Renjin doesn't support loading external packages to do calculation... sigh...
		for (int i=1; i<coefVectorJava.size(); i++) {
			if (coefVectorJava.size()<=2) { // only one predictor
				VIFvector = null;
				return;
			}
			String varName = "X" + Integer.toString(i+1); // first iteration should be X2
			// remove the dependent variable (X1)
			engine.eval("removed_one_df <- subset(new_df, select=-c(X1))");
			// regress Xi on the rest
			engine.eval(
					String.format("model2 = lm(%s ~ ., data=removed_one_df)", varName)
					);
			// get R^2 for the current predictor. Then VIF_i = 1/(1-R^2)
			DoubleVector currentVIF = (DoubleVector)engine.eval("1/(1-summary(model2)$r.squared)");
			VIFvector.add(currentVIF.getElementAsDouble(0));
		}
	}
}
