package com.example.entiy;
import java.io.Serializable;
import java.util.List;
public class Weather implements Serializable{

	private static final long serialVersionUID = 6969649864231736028L;
	private boolean isNight;
	private String city;
	private List<String> temperatureMax;
	private List<String> temperatureMin;
	private List<Integer> maxlist;
	private List<Integer> minlist;
	public boolean isNight() {
		return isNight;
	}
	public void setNight(boolean isNight) {
		this.isNight = isNight;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public List<String> getTemperatureMax() {
		return temperatureMax;
	}
	public void setTemperatureMax(List<String> temperatureMax) {
		this.temperatureMax = temperatureMax;
	}
	public List<String> getTemperatureMin() {
		return temperatureMin;
	}
	public void setTemperatureMin(List<String> temperatureMin) {
		this.temperatureMin = temperatureMin;
	}
	public List<Integer> getMaxlist() {
		return maxlist;
	}
	public void setMaxlist(List<Integer> maxlist) {
		this.maxlist = maxlist;
	}
	public List<Integer> getMinlist() {
		return minlist;
	}
	public void setMinlist(List<Integer> minlist) {
		this.minlist = minlist;
	}
	@Override
	public String toString() {
		return "Weather [isNight=" + isNight + ", city=" + city
				+ ", temperatureMax=" + temperatureMax + ", temperatureMin="
				+ temperatureMin + ", maxlist=" + maxlist + ", minlist="
				+ minlist + "]";
	}
}
