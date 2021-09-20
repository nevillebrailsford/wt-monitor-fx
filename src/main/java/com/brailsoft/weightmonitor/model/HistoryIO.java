package com.brailsoft.weightmonitor.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class HistoryIO {
	private static HistoryIO instance = null;
	private static History history;

	public synchronized static HistoryIO getInstance() {
		if (instance == null) {
			instance = new HistoryIO();
			history = History.getInstance();
		}
		return instance;
	}

	private HistoryIO() {
	}

	public void loadWeightFile(String fn) {
		try (BufferedReader inputFile = new BufferedReader(new FileReader(fn))) {
			do {
				String s = inputFile.readLine();
				Observation o = new Observation(getDate(s), getWeight(s));
				history.addObservation(o);
			} while (inputFile.ready());
		} catch (Exception e) {
		}
	}

	public void saveWeightFile(String fn) {
		try (PrintWriter outputFile = new PrintWriter(new BufferedWriter(new FileWriter(fn)))) {
			List<Observation> observations = history.getHistory();
			for (int i = 0; i < observations.size(); i++) {
				outputFile.println(observations.get(i));
			}
			outputFile.flush();
		} catch (Exception e) {
		}
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
