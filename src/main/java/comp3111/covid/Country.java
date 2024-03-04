package comp3111.covid;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import edu.duke.FileResource;
import javafx.scene.chart.XYChart;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Class for parsing and storing the data of a country
 */
public class Country {

    private final String name;
    private String isoCode;
    private ArrayList<Long> totalCases;
    private ArrayList<Float> totalCases1M;
    private ArrayList<Long> totalDeaths;
    private ArrayList<Float> totalDeaths1M;
    private ArrayList<Long> totalVacNum;
    private ArrayList<Float> totalVacRate;
    private ArrayList<Long> totalFullyVacNum;
    private ArrayList<Float> totalFullyVacRate;
    private ArrayList<Long> newVacNum;
    private ArrayList<Float> newVacNumRate;
    private ArrayList<Long> C2FullyVacNum;
    private ArrayList<Float> C2FullyVacRate;
    private Date minDate;
    private long population;
    private long maxDaysIndex;
    private Date recentDate;
    public static String dataSetName = "COVID_Dataset_v1.0.csv";
    private static boolean isInit;
    private static FileResource fr;
    public static void init() {
        isInit = true;
        fr = new FileResource("dataset/" + dataSetName);
    }
    /**
     * Default Constructor
     */
    public Country() {
        assert isInit;
        name = "Blank";
        isoCode = "";
        population = 0;
    }
    public Country(String countryName) throws ParseException {
        this(countryName, "Unitialized");
    }

    /**
     * Constructor for Country Class
     *
     * @param countryName The name of the Country or Area
     * @throws ParseException Fail to parse the date in CSV file
     */
    public Country(String countryName, String countryISO) throws ParseException {
        assert isInit;
        // initialization
        name = countryName;
        isoCode = countryISO;
        totalCases = new ArrayList<Long>();
        totalDeaths = new ArrayList<Long>();
        totalVacNum = new ArrayList<Long>();
        totalCases1M = new ArrayList<Float>();
        totalDeaths1M = new ArrayList<Float>();
        totalVacRate = new ArrayList<Float>();
        totalFullyVacNum = new ArrayList<Long>();
        totalFullyVacRate = new ArrayList<Float>();
        C2FullyVacNum = new ArrayList<Long>();
        C2FullyVacRate = new ArrayList<Float>();
        newVacNum = new ArrayList<Long>();
        newVacNumRate = new ArrayList<Float>();
        population = 0;
        // eg 2/24/2020
        String iDataset = "COVID_Dataset_v1.0.csv"; // hard code this for now

        boolean gotMinDate = false;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date dateExpected = new Date();

        long previousVacNum = 0L;
        long previousFullyVacNum = 0L;
        long previousNewVacNum = 0L;

        CSVParser parser = fr.getCSVParser(true);
        for (CSVRecord rec : parser) {
            if (rec.get("location").equals(countryName) || rec.get("iso_code").equals(countryISO)) {

                String s_deaths = rec.get("total_deaths");
                String s_cases = rec.get("total_cases");
                String s_cases_1m = rec.get("total_cases_per_million");
                String s_deaths_1m = rec.get("total_deaths_per_million");
                String s_fullyVacNum = rec.get("people_fully_vaccinated");
                String s_vacNum = rec.get("people_vaccinated");
                String s_newVacNum = rec.get("new_vaccinations_smoothed");
                String s_pop = rec.get("population");

                if (isoCode.equals("Uninitialized")) {
                    isoCode = rec.get("iso_code");
                }
                if (!gotMinDate) {
                    gotMinDate = true;
                    String inputString = rec.get("date");
                    minDate = dateFormat.parse(inputString);
                    dateExpected = minDate;
                } else {
                    dateExpected = Date.from(dateExpected.toInstant().plus(1, ChronoUnit.DAYS));
                    String inputString = rec.get("date");
                    Date dateInData = dateFormat.parse(inputString);
                    // Fill in the date gap with the previous value
                    for (Date d = dateExpected; !dateInData.equals(d); d = Date.from(d.toInstant().plus(1, ChronoUnit.DAYS))) {
                        totalCases.add(totalCases.get(totalCases.size() - 1));
                        totalCases1M.add(totalCases1M.get(totalCases1M.size() - 1));
                        totalDeaths.add(totalDeaths.get(totalDeaths.size() - 1));
                        totalDeaths1M.add(totalDeaths1M.get(totalDeaths1M.size() - 1));
                        totalVacNum.add(totalVacNum.get(totalVacNum.size() - 1));
                        totalFullyVacNum.add(totalFullyVacNum.get(totalFullyVacNum.size() - 1));
                        totalVacRate.add(totalVacRate.get(totalVacRate.size() - 1));
                        totalFullyVacRate.add(totalFullyVacRate.get(totalFullyVacRate.size() - 1));
                        C2FullyVacNum.add(-1L);
                        C2FullyVacRate.add(-1.0f);
                    }
                    dateExpected = dateInData;
                }
                if (s_cases.equals("")) {
                    totalCases.add(0L);
                } else {
                    totalCases.add(Long.parseLong(s_cases));
                }
                if (s_cases_1m.equals("")) {
                    totalCases1M.add(0.0f);
                } else {
                    totalCases1M.add(Float.parseFloat(s_cases_1m));
                }

                // there may be cases but no deaths
                if (s_deaths.equals("")) {
                    totalDeaths.add(0L);
                } else {
                    totalDeaths.add(Long.parseLong(s_deaths));
                }
                if (s_deaths_1m.equals("")) {
                    totalDeaths1M.add(0.0f);
                } else {
                    totalDeaths1M.add(Float.parseFloat(s_deaths_1m));
                }
                
                if (!s_pop.equals("")) {
                	population = Long.parseLong(s_pop);
                } else if (countryName.equals("International")) {
                	population = 79000000000L;
                } else if (countryName.equals("Northern Cyprus")) {
                	population = 326000L; //from Wikipedia
                } //There are two countries with no population, handled them here
                
                // there may be empty values for vaccination num, then use previous values
                if (s_vacNum.equals("")) {
                    totalVacNum.add(previousVacNum);
                    totalVacRate.add((float) previousVacNum / population);
                } else {
                    previousVacNum = Long.parseLong(s_vacNum);
                    totalVacNum.add(Long.parseLong(s_vacNum));
                    totalVacRate.add(Float.parseFloat(s_vacNum) / population);
                }
                // similar to above
                if (s_fullyVacNum.equals("")) {
                    totalFullyVacNum.add(previousFullyVacNum);
                    C2FullyVacNum.add(-1L);
                    totalFullyVacRate.add((float) previousFullyVacNum / population);
                    C2FullyVacRate.add(-1.0f);
                } else {
                    previousFullyVacNum = Long.parseLong(s_fullyVacNum);
                    totalFullyVacNum.add(Long.parseLong(s_fullyVacNum));
                    C2FullyVacNum.add(Long.parseLong(s_fullyVacNum));
                    totalFullyVacRate.add(Float.parseFloat(s_fullyVacNum) / population);
                    C2FullyVacRate.add(Float.parseFloat(s_fullyVacNum) / population);
                }
                if (s_newVacNum.equals("")) {
                    newVacNum.add(previousNewVacNum);
                    newVacNumRate.add((float) previousNewVacNum / population);
                } else {
                    previousNewVacNum = Long.parseLong(s_newVacNum);
                    newVacNum.add(Long.parseLong(s_newVacNum));
                    newVacNumRate.add(Float.parseFloat(s_newVacNum) / population);
                }
            }else if (gotMinDate){
                break;
            }
        }
        recentDate = dateExpected;
        maxDaysIndex = ChronoUnit.DAYS.between(minDate.toInstant(), dateExpected.toInstant());
    }

    /**
     * Parse the difference between date and min date to get the index
     *
     * @param date date that wanted to parse
     * @return day difference (index for the data)
     * @throws DateIncorrectException the input date is incorrect, error code included
     */
    public long dayDiffParse(Date date) throws DateIncorrectException {
        if (date.before(this.minDate)) {
            // chosen date should at least be minDate
            throw new DateIncorrectException(DateIncorrectException.ErrCode.LOWER_THAN_MIN);
        }
        // counts number of days difference between user date and min date
        long day_diff = ChronoUnit.DAYS.between(this.minDate.toInstant(), date.toInstant());
        if (day_diff > Integer.MAX_VALUE) {
            // day_diff too big to convert
            throw new DateIncorrectException(DateIncorrectException.ErrCode.LARGER_THAN_INT);
        }
        if (day_diff > maxDaysIndex)
            // day_diff larger than maximum index
            return maxDaysIndex;
        return day_diff;
    }

    /**
     * get total cases by date
     *
     * @param d date
     * @return total cases
     * @throws DateIncorrectException incorrect date input
     */
    // given a Date object, returns the total cases at that date
    public long getTotalCasesByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalCases.get((int) day_diff);
    }

    /**
     * get total cases by date per one million
     *
     * @param d date
     * @return total cases per one million
     * @throws DateIncorrectException incorrect date input
     */
    public float getTotalCases1MByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalCases1M.get((int) day_diff);
    }

    /**
     * get total deaths by date
     *
     * @param d date
     * @return total deaths
     * @throws DateIncorrectException incorrect date input
     */
    public long getTotalDeathsByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalDeaths.get((int) day_diff);
    }

    /**
     * get total deaths by date per one million
     *
     * @param d date
     * @return total deaths per one million
     * @throws DateIncorrectException incorrect date input
     */
    public float getTotalDeaths1MByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalDeaths1M.get((int) day_diff);
    }

    /**
     * get total number of vaccinations by date
     *
     * @param d date
     * @return total number of vaccinations
     * @throws DateIncorrectException incorrect date input
     */
    public long getTotalVacNumByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalVacNum.get((int) day_diff);
    }

    /**
     * get total number of fully vaccinations by date
     *
     * @param d date
     * @return total number of vaccinations
     * @throws DateIncorrectException incorrect date input
     */
    public long getFullyVacNumByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalFullyVacNum.get((int) day_diff);
    }
    /**
     * get total number of fully vaccinations by date for task C2(ignore missing values)
     *
     * @param d date
     * @return total number of vaccinations
     * @throws DateIncorrectException incorrect date input
     */
    public long getC2VacNumByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return C2FullyVacNum.get((int) day_diff);
    }
    /**
     * get vaccination rate by date
     *
     * @param d date
     * @return vaccination rate
     * @throws DateIncorrectException incorrect date input
     */
    public float getTotalVacRateByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalVacRate.get((int) day_diff);
    }

    /**
     * get fully vaccination rate by date
     *
     * @param d date
     * @return vaccination rate
     * @throws DateIncorrectException incorrect date input
     */
    public float getFullyVacRateByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return totalFullyVacRate.get((int) day_diff);
    }
    /**
     * get fully vaccination rate by date for task C2 (ignore missing values)
     *
     * @param d date
     * @return vaccination rate
     * @throws DateIncorrectException incorrect date input
     */
    public float getC2VacRateByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return C2FullyVacRate.get((int) day_diff);
    }
    /**
     * get new vaccination (smoothed) number by date
     *
     * @param d date
     * @return vaccination rate
     * @throws DateIncorrectException incorrect date input
     */
    public Long getNewVacNumByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return newVacNum.get((int) day_diff);
    }
    /**
     * get new vaccination (smoothed) rate by date
     *
     * @param d date
     * @return vaccination rate
     * @throws DateIncorrectException incorrect date input
     */
    public float getNewVacRateByDate(Date d) throws DateIncorrectException {
        long day_diff = dayDiffParse(d);
        return newVacNumRate.get((int) day_diff);
    }
    /**
     * get the name of Country
     *
     * @return string of name
     */
    public String getName() {
        return this.name;
    }

    /**
     * get the iso Code
     *
     * @return string of isoCode
     */
    public String getIsoCode() {
        return isoCode;
    }

    /**
     * get the max index of days
     *
     * @return max index (length - 1)
     */
    public long getMaxDaysIndex() {
        return maxDaysIndex;
    }

    /**
     * get the population
     *
     * @return population
     */
    public long getPopulation() {
        return population;
    }

    /**
     * get the minimum date
     *
     * @return Date
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * get the most recent date
     *
     * @return Date
     */
    public Date getRecentDate() {
        return recentDate;
    }

}