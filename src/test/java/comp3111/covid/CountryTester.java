package comp3111.covid;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;

import java.time.temporal.ChronoUnit;

public class CountryTester {
	Country testCountry;
	Date testDate;
	@Before
	public void setUp() throws Exception {
		Country.init();
		testDate = new GregorianCalendar(2021, Calendar.DECEMBER, 1).getTime();
		testCountry = new Country("Portugal");
		testCountry.getName();
	}
	@Test
	public void countryCreationTest() {
		assertEquals("Portugal", testCountry.getName());
	}
	@Test
	public void totalCasesTest() {
		try {
			assertEquals(935246, testCountry.getTotalCasesByDate(testDate));
		} catch (DateIncorrectException err) {
			fail();
		}
	}
	@Test
	public void totalCases1MTest() {
		try {
			Date currDate = new GregorianCalendar(2020, Calendar.MARCH, 2).getTime();
			// when comparing two floats a and b the arguments are (a, b, delta) (delta is the minimum difference expected)
			assertEquals(testCountry.getTotalCases1MByDate(currDate), 0.196f, 0.001);
		} catch (DateIncorrectException err) {
			fail();
		}
	}
	@Test
	public void minDayTest() {
		try {
			assertTrue(testCountry.getTotalCasesByDate(testCountry.getMinDate()) >= 0);
		} catch (DateIncorrectException err) {
			fail();
		}
	}
	@Test
	public void totalDeathsTest() {
		try {
			assertTrue(testCountry.getTotalDeathsByDate(testDate) >= 0);
		} catch (DateIncorrectException err) {
			fail();
		}
	}
	@Test
	public void totalDeaths1MTest() {
		try {
			Date currDate = new GregorianCalendar(2020, Calendar.MARCH, 17).getTime();
			// when comparing two floats a and b the arguments are (a, b, delta) (delta is the minimum difference expected)
			assertEquals(testCountry.getTotalDeaths1MByDate(currDate), 0.098f, 0.001);
		} catch (DateIncorrectException err) {
			fail();
		}
	}
	@Test
	public void totalVacTest() {
		try {
			assertTrue(testCountry.getTotalVacNumByDate(testDate) >= 0);
		} catch (DateIncorrectException err) {
			fail();
		}
	}
	@Test
	public void populationNotEmptyTest() {
		// 'International' has no recorded population value but DOES have total cases, total deaths, etc
		try {
			Country testInternational = new Country("International");
			assertEquals(testInternational.getPopulation(), 79000000000L);
		} catch (ParseException p) {
			fail();
		}
	}
	@Test
	public void populationNotEmptyTest2() {
		// 'International' has no recorded population value but DOES have total cases, total deaths, etc
		try {
			Country testNC = new Country("Northern Cyprus");
			assertEquals(testNC.getPopulation(), 326000L);
		} catch (ParseException p) {
			fail();
		}
	}
	@Test
	public void consecutiveDaysInputTest() {
		try {
			Country testPortugal = new Country("Portugal");
			Date minDate = testPortugal.getMinDate();
			Date minDatePlusOne = Date.from(minDate.toInstant().plus(1, ChronoUnit.DAYS));
			// check that above is 1/07/2020 (Jan 7 2020)
			assertEquals(testPortugal.getTotalDeathsByDate(minDatePlusOne),0);
		} catch (Exception e) {
			fail();
		}
	}
}