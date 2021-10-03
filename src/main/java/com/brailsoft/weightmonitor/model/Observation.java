package com.brailsoft.weightmonitor.model;

import java.text.DecimalFormat;
import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Observation implements Comparable<Observation> {
	private StringProperty date = new SimpleStringProperty(this, "date", "");
	private StringProperty weight = new SimpleStringProperty(this, "weight", "");

	private long key;

	private static long count = 1;
	public static String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
			"Nov", "Dec" };

	public static void resetCount() {
		count = 1;
	}

	public static String month(int m) {
		return months[m - 1];
	}

	public static String month(String m) {
		return month(Integer.valueOf(m).intValue());
	}

	public Observation(String date, String weight) {
		if (invalidWeightFormat(weight)) {
			throw new IllegalArgumentException("Weight '" + weight + "' is not a valid weight.");
		}
		if (invalidDateFormat(date)) {
			throw new IllegalArgumentException("Date '" + date + "' is not a valid date.");
		}
		key = count++;
		this.date.set(date);
		this.weight.set(weight);
	}

	public Observation(Observation other) {
		this.key = other.getKey();
		this.date.set(other.getDate());
		this.weight.set(other.getWeight());
	}

	public long getKey() {
		return key;
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String getDate() {
		return date.get();
	}

	public StringProperty dateProperty() {
		return date;
	}

	public String getYear() {
		return getDate().substring(0, 4);
	}

	public String getMonth() {
		return getDate().substring(5, 7);
	}

	public void setWeight(String weight) {
		this.weight.set(weight);
	}

	public String getWeight() {
		return weight.get();
	}

	public StringProperty weightProperty() {
		return weight;
	}

	private String formLine(String d, String w) {
		int lineLength = 19;
		String s = d;
		w = new DecimalFormat("0.0").format(java.lang.Double.valueOf(w).doubleValue());
		for (int i = 0; i < lineLength - 10 - w.length(); i++) {
			s += " ";
		}
		s += w;
		return s;
	}

	private boolean invalidWeightFormat(String weight) {
		if (weight.isEmpty()) {
			return true;
		}
		boolean dotFound = false;
		for (int i = 0; i < weight.length(); i++) {
			char ch = weight.charAt(i);
			if (Character.isDigit(ch) || ch == '.') {
				if (ch == '.') {
					if (dotFound) {
						return true;
					} else {
						dotFound = true;
					}
				}
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean invalidDateFormat(String date) {
		if (date.isEmpty()) {
			return true;
		}
		if (!date.matches("\\d\\d\\d\\d/\\d\\d/\\d\\d")) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return formLine(date.get(), weight.get());
	}

	@Override
	public int compareTo(Observation other) {
		if (other == null) {
			throw new IllegalArgumentException("Cannot compare with null");
		}
		String thisReading = this.toString();
		String otherReading = other.toString();
		return thisReading.compareTo(otherReading);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDate(), getWeight());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Observation other = (Observation) obj;
		return Objects.equals(getDate(), other.getDate());
	}

}
