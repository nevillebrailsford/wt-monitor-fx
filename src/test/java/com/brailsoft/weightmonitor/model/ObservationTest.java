/**
 * 
 */
package com.brailsoft.weightmonitor.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author nevil
 *
 */
class ObservationTest {

	Observation obs1 = new Observation("2021/08/29", "70.0");
	Observation obs2 = null;
	Observation today = new Observation("2021/08/29", "70.0");
	Observation yesterday = new Observation("2021/08/28", "70.0");
	Observation tomorrow = new Observation("2021/08/30", "70.0");
	Observation lighter = new Observation("2021/08/29", "69.9");
	Observation heavier = new Observation("2021/08/29", "70.1");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		Observation.resetCount();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.brailsoft.weightmonitorfx.model.Observation#Observation(String date, String weight)}.
	 */
	@Test
	void testObservation() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/29", "");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/29", "a");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/29", "70a");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/29", "70..");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/29", "70.0.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("a", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("20210829", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/0829", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("20211/08/29", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/081/29", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/291", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("2021/08/29/", "70.0");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			new Observation("/2021/08/29", "70.0");
		});
	}

	/**
	 * Test method for
	 * {@link com.brailsoft.weightmonitorfx.model.Observation#Observation(com.brailsoft.weightmonitorfx.model.Observation)}.
	 */
	@Test
	void testObservationObservation() {
		obs2 = new Observation(obs1);
		assertEquals("2021/08/29", obs2.getDate());
		assertEquals("70.0", obs1.getWeight());
		assertFalse(obs1 == obs2);
		assertTrue(obs1.compareTo(obs2) == 0);
		assertTrue(obs1.equals(obs2));
		assertTrue(obs2.equals(obs1));
	}

	/**
	 * Test method for
	 * {@link com.brailsoft.weightmonitorfx.model.Observation#getDate()}.
	 */
	@Test
	void testGetDate() {
		assertEquals("2021/08/29", obs1.getDate());
	}

	/**
	 * Test method for
	 * {@link com.brailsoft.weightmonitorfx.model.Observation#getWeight()}.
	 */
	@Test
	void testGetWeight() {
		assertEquals("70.0", obs1.getWeight());
	}

	/**
	 * Test method for
	 * {@link com.brailsoft.weightmonitorfx.model.Observation#toString()}.
	 */
	@Test
	void testToString() {
		assertEquals("2021/08/29     70.0", obs1.toString());
	}

	/**
	 * Test method for
	 * {@link com.brailsoft.weightmonitorfx.model.Observation#compareTo(com.brailsoft.weightmonitorfx.model.Observation)}.
	 */
	@Test
	void testCompareTo() {
		assertTrue(today.compareTo(today) == 0);
		assertTrue(today.compareTo(yesterday) > 0);
		assertTrue(today.compareTo(tomorrow) < 0);
		assertTrue(today.compareTo(lighter) > 0);
		assertTrue(today.compareTo(heavier) < 0);
	}

}
