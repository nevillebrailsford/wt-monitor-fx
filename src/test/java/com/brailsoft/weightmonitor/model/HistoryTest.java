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
	private Observation obs1 = new Observation("2021/08/29", "71.0");
	private Observation obs2 = new Observation("2021/08/29", "71.1");
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
		assertEquals(obs2, temp.getHistory().get(0));
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

}
