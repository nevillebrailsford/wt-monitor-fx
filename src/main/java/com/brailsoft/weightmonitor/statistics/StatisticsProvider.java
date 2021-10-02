package com.brailsoft.weightmonitor.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.brailsoft.weightmonitor.model.History;
import com.brailsoft.weightmonitor.model.Observation;

public class StatisticsProvider {

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
		if (getNumberOfObservations() > 0) {
			for (Observation o : History.getInstance().getHistory()) {
				result += Double.valueOf(o.getWeight()).doubleValue();
			}
			result = result / getNumberOfObservations();
		}
		return result;
	}

	public static double getMaximumWeight() {
		double result = 0.0d;
		for (Observation o : History.getInstance().getHistory()) {
			result = Math.max(result, Double.valueOf(o.getWeight()).doubleValue());
		}
		return result;
	}

	public static double getMinimumWeight() {
		double result = 0.0d;
		if (getNumberOfObservations() > 0) {
			result = Double.MAX_VALUE;
		}
		for (Observation o : History.getInstance().getHistory()) {
			result = Math.min(result, Double.valueOf(o.getWeight()).doubleValue());
		}
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
