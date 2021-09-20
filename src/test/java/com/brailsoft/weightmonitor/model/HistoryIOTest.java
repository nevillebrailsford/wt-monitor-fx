package com.brailsoft.weightmonitor.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HistoryIOTest {

	@TempDir
	File weightFileDirectory;
	static final List<String> lines = Arrays.asList("2021/08/22     72.0", "2021/08/29     71.0",
			"2021/09/05     72.5");
	static HistoryIO historyIO = HistoryIO.getInstance();
	static History history = History.getInstance();
	int index = 0;

	@BeforeEach
	void setUp() throws Exception {
		history.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testLoadWeightFile() throws IOException {
		File weightFile = new File(weightFileDirectory, "wight.wgt");

		Files.write(weightFile.toPath(), lines);

		assertAll(() -> assertTrue(Files.exists(weightFile.toPath())),
				() -> assertLinesMatch(lines, Files.readAllLines(weightFile.toPath())));

		historyIO.loadWeightFile(weightFile.getAbsolutePath());

		assertTrue(history.getHistory().size() == 3);

		index = 0;
		history.getHistory().stream().forEach(o -> {
			assertEquals(o.toString(), lines.get(index++));
		});
	}

	@Test
	void testSaveWeightFile() throws IOException {
		File weightFile = new File(weightFileDirectory, "wight.wgt");

		lines.stream().forEach(s -> {
			history.addObservation(new Observation(getDate(s), getWeight(s)));
		});

		historyIO.saveWeightFile(weightFile.getAbsolutePath());

		assertAll(() -> assertTrue(Files.exists(weightFile.toPath())),
				() -> assertLinesMatch(lines, Files.readAllLines(weightFile.toPath())));

		index = 0;
		Files.readAllLines(weightFile.toPath()).stream().forEach(s -> {
			assertEquals(s, history.getHistory().get(index++).toString());
		});
	}

	private String getDate(String s) {
		s = s.substring(0, 10);
		return s;
	}

	private String getWeight(String s) {
		s = s.substring(10);
		return s.trim();
	}

}
