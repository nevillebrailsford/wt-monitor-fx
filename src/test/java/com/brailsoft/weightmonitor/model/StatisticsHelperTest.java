package com.brailsoft.weightmonitor.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatisticsHelperTest {

	private History temp;
	private Observation obs1 = new Observation("2021/08/29", "10.0");
	private Observation obs2 = new Observation("2021/08/29", "20.0");
	private Observation obs3 = new Observation("2021/08/29", "30.0");
	private Observation obs4 = new Observation("2021/09/29", "10.0");
	private Observation obs5 = new Observation("2021/07/29", "20.0");
	private Observation obs6 = new Observation("2020/12/29", "30.0");

	@BeforeEach
	void setUp() throws Exception {
		temp = History.getInstance();
	}

	@AfterEach
	void tearDown() throws Exception {
		temp.clear();
	}

	@Test
	void testGetHistoryAveragesByMonthForYear() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);

		Map<String, Double> results = StatisticsHelper.getHistoryAveragesByMonthForYear("2020");
		assertEquals(12, results.keySet().size());
		assertEquals(30.0, results.get("Dec"));

		results = StatisticsHelper.getHistoryAveragesByMonthForYear("2021");
		assertEquals(12, results.keySet().size());
		assertEquals(0.0, results.get("Dec"));
		assertEquals(20.0, results.get("Jul"));
		assertEquals(20.0, results.get("Aug"));
		assertEquals(10.0, results.get("Sep"));
	}

	@Test
	void testGetHistoryAveragesByMonthForYearNotExist() {
		Map<String, Double> results = StatisticsHelper.getHistoryAveragesByMonthForYear("2020");
		assertEquals(12, results.keySet().size());
		results.keySet().stream().forEach(s -> {
			assertEquals(0.0, results.get(s));
		});
	}

	@Test
	void testGetHistoryYearsWhenNoObservations() {
		assertEquals(0, StatisticsHelper.getHistoryYears().length);
	}

	@Test
	void testGetHistoryYearsWhenObservationsPresent() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);
		assertEquals(2, StatisticsHelper.getHistoryYears().length);
	}

	@Test
	void testGetFirstRecordedObservationDateWhenNoObservations() {
		assertTrue(StatisticsHelper.getFirstRecordedDate().isBlank());
	}

	@Test
	void testGetFirstRecordedObservationDateWhenObservationsPresent() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);
		assertFalse(StatisticsHelper.getFirstRecordedDate().isBlank());
		assertEquals("2020/12/29", StatisticsHelper.getFirstRecordedDate());
	}

	@Test
	void testGetLastRecordedObservationDateWhenNoObservations() {
		assertTrue(StatisticsHelper.getLastRecordedDate().isBlank());
	}

	@Test
	void testGetLastRecordedObservationDateWhenObservationsPresent() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);

		assertFalse(StatisticsHelper.getLastRecordedDate().isBlank());
		assertEquals("2021/09/29", StatisticsHelper.getLastRecordedDate());
	}

	@Test
	void testGetFirstRecordedObservationWeightWhenNoObservations() {
		assertTrue(StatisticsHelper.getFirstRecordedWeight().isBlank());
	}

	@Test
	void testGetFirstRecordedObservationWeightWhenObservationsPresent() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);
		assertFalse(StatisticsHelper.getFirstRecordedWeight().isBlank());
		assertEquals("30.0", StatisticsHelper.getFirstRecordedWeight());
	}

	@Test
	void testGetLastRecordedObservationWeightWhenNoObservations() {
		assertTrue(StatisticsHelper.getLastRecordedWeight().isBlank());
	}

	@Test
	void testGetLastRecordedObservationWeightWhenObservationsPresent() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);

		assertFalse(StatisticsHelper.getLastRecordedWeight().isBlank());
		System.out.println(temp.getHistory());
		assertEquals("10.0", StatisticsHelper.getLastRecordedWeight());
	}
}
