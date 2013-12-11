package com.example.weather;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.common.HttpHelper;
import com.example.entiy.Weather;

import android.app.Activity;
/**
 * 解析天气数据
 * @author Dave
 *
 */
public class WeatherData {
	private Activity activity;
	public WeatherData(Activity activity){
		this.activity = activity;
	}
	public Weather getData(String str){
		return parseJson(str);
	}
	private Weather parseJson(String strResult) {
		Weather weather = null;
		try {
			JSONObject jsonObj = new JSONObject(strResult).getJSONObject("weatherinfo");
			weather = new Weather();
			int ftime = jsonObj.getInt("fchh"); // 更新时间（整点）【更新时间确定temp属于哪天】
			int temp = 0;	//偏移
			if(ftime >= 18 || ftime < 8){
				weather.setNight(true);
				temp = 1;
			}
			weather.setCity(jsonObj.getString("city"));	//城市
			
			List<String> tempList = new ArrayList<String>();	//未来六天温度
			tempList.add(jsonObj.getString("temp1"));
			tempList.add(jsonObj.getString("temp2"));
			tempList.add(jsonObj.getString("temp3"));
			tempList.add(jsonObj.getString("temp4"));
			tempList.add(jsonObj.getString("temp5"));
			tempList.add(jsonObj.getString("temp6"));
			
			List<String> tempListMax = new ArrayList<String>();		//未来六天最高温度集合（有℃符号）
			tempListMax.add(getTemperatureMaxAndMin(tempList.get(0))[0+temp]);
			tempListMax.add(getTemperatureMaxAndMin(tempList.get(1))[0+temp]);
			tempListMax.add(getTemperatureMaxAndMin(tempList.get(2))[0+temp]);
			tempListMax.add(getTemperatureMaxAndMin(tempList.get(3))[0+temp]);
			tempListMax.add(getTemperatureMaxAndMin(tempList.get(4))[0+temp]);
			tempListMax.add(getTemperatureMaxAndMin(tempList.get(5))[0+temp]);
			weather.setTemperatureMax(tempListMax);		
			
			
			List<String> tempListMin = new ArrayList<String>();			//未来六天最低温度集合（有℃符号）
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(0))[1-temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(1))[1-temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(2))[1-temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(3))[1-temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(4))[1-temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(5))[1-temp]);
			weather.setTemperatureMin(tempListMin);
			
			
			weather.setMaxlist(transplate(tempListMax));	//未来六天最高温度集合（无℃符号）
			weather.setMinlist(transplate(tempListMin));	//未来六天最低温度集合（无℃符号）
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weather;
	}
	//获取最高温度和最低温度，有℃符号
	private String[] getTemperatureMaxAndMin(String str){
		return str.split("~");
	}
	//去除最高温度和最低温度里的℃符号
	private List<Integer> transplate(List<String> strList){
		List<Integer> intList = new ArrayList<Integer>();
		for(String temp : strList){
			intList.add(Integer.valueOf(temp.split("℃")[0]));
		}
		return intList;
	}
}

