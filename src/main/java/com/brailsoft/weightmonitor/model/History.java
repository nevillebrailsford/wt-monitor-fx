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
		observations.put(observation.getKey(), new Observation(observation));
	}

	public synchronized void changeObservation(Observation observation) {
		var obs = observations.get(observation.getKey());
		if (obs != null) {
			observations.put(observation.getKey(), new Observation(observation));
		}
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
		getHistory().stream().forEach(o -> {
			if (o.getYear().equals(year)) {
				copyList.add(new Observation(o));
			}
		});
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized Map<String, List<Observation>> getHistoryByMonthForYear(String year) {
		Map<String, List<Observation>> result = new HashMap<>();
		getHistoryForYear(year).stream().forEach(o -> {
			List<Observation> list = result.get(Observation.month(o.getMonth()));
			if (list == null) {
				list = new ArrayList<>();
				result.put(Observation.month(o.getMonth()), list);
			}
			list.add(new Observation(o));
		});
		return result;
	}

}
