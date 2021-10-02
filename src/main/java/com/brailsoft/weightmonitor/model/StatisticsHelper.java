package com.brailsoft.weightmonitor.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StatisticsHelper {

	public static Map<String, Double> getHistoryAveragesByMonthForYear(String year) {
		Map<String, Double> result = new HashMap<>();

		Map<String, List<Observation>> observationsForYear = History.getInstance().getHistoryByMonthForYear(year);

		for (int i = 0; i < Observation.months.length; i++) {
			List<Observation> obs = observationsForYear.get(Observation.months[i]);
			double average = 0.0d;
			double total = 0.0d;
			double count = 0.0d;
			for (Observation o : obs) {

				total += Double.valueOf(o.getWeight()).doubleValue();
				count++;
			}
			if (count > 0) {
				average = total / count;
			}
			result.put(Observation.months[i], average);
		}

		return result;
	}

	public static String[] getHistoryYears() {
		List<String> years = History.getInstance().getYears();
		String[] result = years.toArray(new String[] {});
		return result;
	}

	public static String getFirstRecordedDate() {
		Optional<Observation> obs = getFirstRecordedObservation();
		String result = obs.isPresent() ? obs.get().getDate() : "";
		return result;
	}

	public static String getLastRecordedDate() {
		Optional<Observation> obs = getLastRecordedObservation();
		String result = obs.isPresent() ? obs.get().getDate() : "";
		return result;
	}

	public static String getFirstRecordedWeight() {
		Optional<Observation> obs = getFirstRecordedObservation();
		String result = obs.isPresent() ? obs.get().getWeight() : "";
		return result;
	}

	public static String getLastRecordedWeight() {
		Optional<Observation> obs = getLastRecordedObservation();
		String result = obs.isPresent() ? obs.get().getWeight() : "";
		return result;
	}

	public static double getAverageWeight() {
		double result = 0.0d;
		return result;
	}

	private static Optional<Observation> getFirstRecordedObservation() {
		Optional<Observation> result = getNumberedObservation(1);
		return result;
	}

	private static Optional<Observation> getLastRecordedObservation() {
		Optional<Observation> result = getNumberedObservation(getNumberOfObservations());
		return result;
	}

	private static Optional<Observation> getNumberedObservation(int index) {
		List<Observation> obs = History.getInstance().getHistory();
		Observation o = null;
		if (index > 0 && obs.size() >= index)
			o = obs.get(index - 1);
		Optional<Observation> result = Optional.ofNullable(o);
		return result;
	}

	private static int getNumberOfObservations() {
		return History.getInstance().getHistory().size();
	}
}
