package com.brailsoft.weightmonitor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class History {
	private static History instance = null;
	private ObservableMap<Long, Observation> observations = null;

	public synchronized static History getInstance() {
		if (instance == null) {
			instance = new History();
		}
		return instance;
	}

	private History() {
		observations = FXCollections.observableMap(new ConcurrentHashMap<>());
	}

	public synchronized void addListener(MapChangeListener<? super Long, ? super Observation> l) {
		observations.addListener(l);
	}

	public synchronized void removeListener(MapChangeListener<? super Long, ? super Observation> l) {
		observations.removeListener(l);
	}

	public synchronized void addObservation(Observation observation) {
		if (observations.containsValue(observation)) {
			changeObservation(observation);
		} else {
			observations.put(observation.getKey(), new Observation(observation));
		}
	}

	public synchronized void changeObservation(Observation observation) {
		Observation obs = null;
		for (Observation o : observations.values()) {
			if (o.equals(observation)) {
				obs = o;
			}
		}
		if (obs != null) {
			observations.remove(obs.getKey());
		}
		observations.put(observation.getKey(), new Observation(observation));
	}

	public synchronized void removeObservation(Observation observation) {
		observations.remove(observation.getKey());
	}

	public synchronized void clear() {
		observations.clear();
	}

	public synchronized List<String> getYears() {
		List<String> copyList = new ArrayList<>();
		Set<String> yearSet = new HashSet<>();
		observations.values().stream().forEach(o -> {
			yearSet.add(o.getYear());
		});
		copyList.addAll(yearSet);
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized List<Observation> getHistory() {
		List<Observation> copyList = new ArrayList<>();
		observations.values().stream().forEach(o -> {
			copyList.add(new Observation(o));
		});
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized List<Observation> getHistoryForYear(String year) {
		List<Observation> copyList = new ArrayList<>();
		getHistory().stream().filter(o -> o.getYear().equals(year)).forEach(o -> {
			copyList.add(new Observation(o));
		});
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized Map<String, List<Observation>> getHistoryByMonthForYear(String year) {
		Map<String, List<Observation>> result = new HashMap<>();
		for (int i = 0; i < Observation.months.length; i++) {
			result.put(Observation.months[i], new ArrayList<Observation>());
		}
		getHistoryForYear(year).stream().forEach(o -> {
			List<Observation> list = result.get(Observation.month(o.getMonth()));
			list.add(new Observation(o));
		});
		return result;
	}

}
