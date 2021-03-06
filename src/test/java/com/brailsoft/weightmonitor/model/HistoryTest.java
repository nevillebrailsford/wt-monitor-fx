package com.brailsoft.weightmonitor.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;

class HistoryTest {

	private History temp;
	private Observation obs1 = new Observation("2021/08/28", "71.0");
	private Observation obs2 = new Observation("2021/08/29", "71.1");
	private Observation obs3 = new Observation("2020/08/29", "71.1");
	private Change<? extends Long, ? extends Observation> change = null;
	private MapChangeListener<? super Long, ? super Observation> listener = (
			MapChangeListener.Change<? extends Long, ? extends Observation> c) -> {
		if (c.wasAdded()) {
			assertEquals(obs1, c.getValueAdded());
		} else {
			listenerFailed = true;
			change = c;
		}
	};
	private boolean listenerFailed = false;

	@BeforeEach
	void setUp() throws Exception {
		listenerFailed = false;
		temp = History.getInstance();
	}

	@AfterEach
	void tearDown() throws Exception {
		temp.removeListener(listener);
		temp.clear();
	}

	@Test
	void testGetInstance() {
		assertNotNull(temp);
		History me = History.getInstance();
		assertNotNull(me);
		assertTrue(temp == me);
	}

	@Test
	void testAddListener() {
		temp.addListener(listener);
		temp.addObservation(obs1);
		temp.removeListener(listener);
		if (listenerFailed) {
			fail("Unexpected operation: " + change);
		}
	}

	@Test
	void testRemoveListener() {
		temp.addListener(listener);
		temp.removeListener(listener);
	}

	@Test
	void testAddObservation() {
		assertEquals(0, temp.getHistory().size());
		temp.addObservation(obs1);
		assertEquals(1, temp.getHistory().size());
	}

	@Test
	void testChangeObservation() {
		assertEquals(0, temp.getHistory().size());
		temp.addObservation(obs1);
		assertEquals(1, temp.getHistory().size());
		assertEquals(obs1, temp.getHistory().get(0));
		var change = new Observation(temp.getHistory().get(0));
		change.setWeight("71.1");
		temp.changeObservation(change);
		assertEquals(1, temp.getHistory().size());
	}

	@Test
	void testRemoveObservation() {
		assertEquals(0, temp.getHistory().size());
		temp.addObservation(obs1);
		assertEquals(1, temp.getHistory().size());
		temp.removeObservation(obs1);
		assertEquals(0, temp.getHistory().size());
	}

	@Test
	void testClear() {
		assertEquals(0, temp.getHistory().size());
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		assertEquals(2, temp.getHistory().size());
		temp.clear();
		assertEquals(0, temp.getHistory().size());
	}

	@Test
	void testGetHistory() {
		assertEquals(0, temp.getHistory().size());
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		assertEquals(2, temp.getHistory().size());
		List<Observation> copy = temp.getHistory();
		assertEquals(2, copy.size());
		copy.clear();
		assertEquals(2, temp.getHistory().size());
	}

	@Test
	void testGetYears() {
		assertEquals(0, temp.getYears().size());
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		assertEquals(3, temp.getHistory().size());
		assertEquals(2, temp.getYears().size());
	}

	@Test
	void testGetHistoryForYear() {
		assertEquals(0, temp.getYears().size());
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		assertEquals(3, temp.getHistory().size());
		assertEquals(1, temp.getHistoryForYear("2020").size());
		assertEquals(2, temp.getHistoryForYear("2021").size());
	}

	@Test
	void testGetHistoryByMonthForYear() {
		assertEquals(0, temp.getYears().size());
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);
		assertEquals(3, temp.getHistory().size());

		assertNotNull(temp.getHistoryByMonthForYear("2021"));
		assertNotNull(temp.getHistoryByMonthForYear("2021").keySet());
		assertEquals(12, temp.getHistoryByMonthForYear("2021").keySet().size());
		assertEquals(2, temp.getHistoryByMonthForYear("2021").get("Aug").size());
		assertNotNull(temp.getHistoryByMonthForYear("2020"));
		assertNotNull(temp.getHistoryByMonthForYear("2020").keySet());
		assertEquals(12, temp.getHistoryByMonthForYear("2020").keySet().size());
		assertEquals(1, temp.getHistoryByMonthForYear("2020").get("Aug").size());
		assertNotNull(temp.getHistoryByMonthForYear("2019"));
		assertNotNull(temp.getHistoryByMonthForYear("2019").keySet());
		assertNotNull(temp.getHistoryByMonthForYear("20219"));
		assertEquals(12, temp.getHistoryByMonthForYear("2019").keySet().size());
		assertEquals(0, temp.getHistoryByMonthForYear("2019").get("Aug").size());
	}

	@Test
	void testGetHistoryInCorrectSequence() {
		temp.addObservation(obs1);
		temp.addObservation(obs2);
		temp.addObservation(obs3);

		List<Observation> history = temp.getHistory();
		System.out.println(history);
	}
}
