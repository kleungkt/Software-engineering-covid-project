package comp3111.covid;

import org.apache.commons.csv.*;
import edu.duke.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 *
 * Data Explorer on COVID-19
 * @author <a href=mailto:namkiu@ust.hk>Namkiu Chan</a>
 * @version	1.1
 *
 */
public class DataAnalysis {

	public static CSVParser getFileParser(String dataset) {
		FileResource fr = new FileResource("dataset/" + dataset);
		return fr.getCSVParser(true);
	}


	public static String getConfirmedCases(String dataset, String iso_code) {
		String oReport = "";
		long confirmedCases = 0;
		long numRecord = 0;
		long totalNumRecord = 0;

		for (CSVRecord rec : getFileParser(dataset)) {

			if (rec.get("iso_code").equals(iso_code)) {
				String s = rec.get("new_cases");
				if (!s.equals("")) {
					confirmedCases += Long.parseLong(s);
					numRecord++;
				}
			}
			totalNumRecord++;
		}

		oReport = String.format("Dataset (%s): %,d Records\n\n", dataset, totalNumRecord);
		oReport += String.format("[Summary (%s)]\n", iso_code);
		oReport += String.format("Number of Confirmed Cases: %,d\n", confirmedCases);
		oReport += String.format("Number of Days Reported: %,d\n", numRecord);

		return oReport;
	}

	 public static String getConfirmedDeaths(String dataset, String iso_code) {
			String oReport = "";
			long confirmedDeaths = 0;
			long numRecord = 0;
			long totalNumRecord = 0;

			for (CSVRecord rec : getFileParser(dataset)) {

				if (rec.get("iso_code").equals(iso_code)) {
					String s = rec.get("new_deaths");
					if (!s.equals("")) {
						confirmedDeaths += Long.parseLong(s);
						numRecord++;
					}
				}
				totalNumRecord++;
			}

			oReport = String.format("Dataset (%s): %,d Records\n\n", dataset, totalNumRecord);
			oReport += String.format("[Summary (%s)]\n", iso_code);
			oReport += String.format("Number of Deaths: %,d\n", confirmedDeaths);
			oReport += String.format("Number of Days Reported: %,d\n", numRecord);

			return oReport;
	 }

	/**
	 * get the name list of countries
	 * @param dataset dataset
	 * @return array list of names of countries
	 */
	 public static ArrayList<String> getCountryNameList(String dataset) {
		String current = "";
		ArrayList<String> list = new ArrayList<>();
		for (CSVRecord rec: getFileParser(dataset)){
			String countryName = rec.get("location");
			if(!countryName.equals(current)){
				list.add(countryName);
				current = countryName;
			}
		}
		return list;
	 }

	 // HCJ Task B3
	 public static ArrayList<ArrayList<Float>> getPredictors(String dataset, ArrayList<String> predictorStringArray) {
			String current = ""; // current country being parsed

			ArrayList<ArrayList<Float>> list = new ArrayList<>(); // each element is itself an ArrayList consisting of a given predictor's values for all countries
			// instantiate inner ArrayLists
			for (int i=0; i<predictorStringArray.size(); i++) {
				ArrayList<Float> x = new ArrayList<>();
				list.add(x);
			}

			for (CSVRecord rec: getFileParser(dataset)){
				String countryName = rec.get("location");

				// read the other predictors
				if (!countryName.equals(current)){
					current = countryName;

					// read each predictor and load the appropriate input into the corresponding ArrayList
					for (int j=0; j<predictorStringArray.size(); j++) {
						if (rec.get(predictorStringArray.get(j)).equals("")) {
							list.get(j).add(-1.0f);
						}
						else {
							list.get(j).add(Float.parseFloat(rec.get(predictorStringArray.get(j))));
						}
					}
				}

				// save the most current value of 'total_deaths_per_million'
				if (rec.get(predictorStringArray.get(0)).equals("")) {
					list.get(0).set(list.get(0).size()-1, -1.0f);
				}
				else {
					list.get(0).set(list.get(0).size()-1,
							Float.parseFloat(rec.get(predictorStringArray.get(0))));
				}
			}
			return list;
	 }

	 public static ArrayList<String> getMasterListOfPredictors(String dataset) {
		 CSVParser parser = getFileParser(dataset);
		 Map<String, Integer> header = parser.getHeaderMap();
		 // guaranteed to be in order
		 ArrayList<String> headerList = new ArrayList<>(header.keySet());
		 List<String> x = headerList.subList(43, headerList.size()); // remove indices 0-15, which are figures on ISO code, continent, location, deaths, vacnum and cases
		 ArrayList<String> predictorList = new ArrayList<String>(x);
		 return predictorList;
	 }

	 public static String getRateOfVaccination(String dataset, String iso_code) {
			String oReport = "";
			long fullyVaccinated = 0;
			long numRecord = 0;
			long totalNumRecord = 0;
			long population = 0;
			float rate = 0.0f;

			for (CSVRecord rec : getFileParser(dataset)) {

				if (rec.get("iso_code").equals(iso_code)) {

					String s1 = rec.get("people_fully_vaccinated");
					String s2 = rec.get("population");
					if (!s1.equals("") && !s2.equals("")) {
						fullyVaccinated = Long.parseLong(s1);
						population = Long.parseLong(s2);
						numRecord++;
					}
				}
				totalNumRecord++;
			}

			if (population !=0)
				rate = (float) fullyVaccinated / population * 100;

			oReport = String.format("Dataset (%s): %,d Records\n\n", dataset, totalNumRecord);
			oReport += String.format("[Summary (%s)]\n", iso_code);
			oReport += String.format("Number of People Fully Vaccinated: %,d\n", fullyVaccinated);
			oReport += String.format("Population: %,d\n", population);
			oReport += String.format("Rate of Vaccination: %.2f%%\n", rate);
			oReport += String.format("Number of Days Reported: %,d\n", numRecord);

			return oReport;
	 }

}