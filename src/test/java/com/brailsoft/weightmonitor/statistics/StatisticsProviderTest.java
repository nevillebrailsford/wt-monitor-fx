package com.brailsoft.weightmonitor.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.weightmonitor.model.History;
import com.brailsoft.weightmonitor.model.Observation;
import com.brailsoft.weightmonitor.statistics.StatisticsProvider;

class StatisticsProviderTest {

	private History temp;

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
		loadObservations();

		Map<String, Double> results = StatisticsProvider.getHistoryAveragesByMonthForYear("2020");
		assertEquals(12, results.keySet().size());
		assertEquals(30.0, results.get("Dec"));

		results = StatisticsProvider.getHistoryAveragesByMonthForYear("2021");
		assertEquals(12, results.keySet().size());
		assertEquals(0.0, results.get("Dec"));
		assertEquals(20.0, results.get("Jul"));
		assertEquals(20.0, results.get("Aug"));
		assertEquals(10.0, results.get("Sep"));
	}

	@Test
	void testGetHistoryAveragesByMonthForYearNotExist() {
		Map<String, Double> results = StatisticsProvider.getHistoryAveragesByMonthForYear("2020");
		assertEquals(12, results.keySet().size());
		results.keySet().stream().forEach(s -> {
			assertEquals(0.0, results.get(s));
		});
	}

	@Test
	void testGetHistoryYearsWhenNoObservations() {
		assertEquals(0, StatisticsProvider.getHistoryYears().length);
	}

	@Test
	void testGetHistoryYearsWhenObservationsPresent() {
		loadObservations();
		assertEquals(2, StatisticsProvider.getHistoryYears().length);
	}

	@Test
	void testGetFirstRecordedObservationDateWhenNoObservations() {
		assertTrue(StatisticsProvider.getFirstRecordedDate().isBlank());
	}

	@Test
	void testGetFirstRecordedObservationDateWhenObservationsPresent() {
		loadObservations();
		assertFalse(StatisticsProvider.getFirstRecordedDate().isBlank());
		assertEquals("2020/12/29", StatisticsProvider.getFirstRecordedDate());
	}

	@Test
	void testGetLastRecordedObservationDateWhenNoObservations() {
		assertTrue(StatisticsProvider.getLastRecordedDate().isBlank());
	}

	@Test
	void testGetLastRecordedObservationDateWhenObservationsPresent() {
		loadObservations();

		assertFalse(StatisticsProvider.getLastRecordedDate().isBlank());
		assertEquals("2021/09/29", StatisticsProvider.getLastRecordedDate());
	}

	@Test
	void testGetFirstRecordedObservationWeightWhenNoObservations() {
		assertTrue(StatisticsProvider.getFirstRecordedWeight().isBlank());
	}

	@Test
	void testGetFirstRecordedObservationWeightWhenObservationsPresent() {
		loadObservations();
		assertFalse(StatisticsProvider.getFirstRecordedWeight().isBlank());
		assertEquals("30.0", StatisticsProvider.getFirstRecordedWeight());
	}

	@Test
	void testGetLastRecordedObservationWeightWhenNoObservations() {
		assertTrue(StatisticsProvider.getLastRecordedWeight().isBlank());
	}

	@Test
	void testGetLastRecordedObservationWeightWhenObservationsPresent() {
		loadObservations();

		assertFalse(StatisticsProvider.getLastRecordedWeight().isBlank());
		System.out.println(temp.getHistory());
		assertEquals("10.0", StatisticsProvider.getLastRecordedWeight());
	}

	@Test
	void testGetAverageWeight() {
		assertEquals(0, StatisticsProvider.getAverageWeight());
		loadObservations();
		assertEquals(20.0, StatisticsProvider.getAverageWeight());
	}

	@Test
	void testGetMaximumWeight() {
		assertEquals(0, StatisticsProvider.getMaximumWeight());
		loadObservations();
		assertEquals(30.0, StatisticsProvider.getMaximumWeight());
	}

	@Test
	void testGetMinimumWeight() {
		assertEquals(0, StatisticsProvider.getMinimumWeight());
		loadObservations();
		assertEquals(10.0, StatisticsProvider.getMinimumWeight());
	}

	private void loadObservations() {
		Observation obs1 = new Observation("2021/08/29", "10.0");
		Observation obs2 = new Observation("2021/08/29", "20.0");
		Observation obs3 = new Observation("2021/08/29", "30.0");
		Observation obs4 = new Observation("2021/09/29", "10.0");
		Observation obs5 = new Observation("2021/07/29", "20.0");
		Observation obs6 = new Observation("2020/12/29", "30.0");
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		temp.addObservation(obs4);
		temp.addObservation(obs5);
		temp.addObservation(obs6);
	}

}
