package com.example.weather;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.example.common.AppException;
import com.example.common.HttpHelper;
import com.example.entiy.Weather;
import com.example.weather.FirstFragment.DownTask;

import android.R.integer;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SecondFragment extends Fragment {
	private Weather weather;// 天气的集合
	private LinearLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DownTask task = new DownTask();
		task.execute("http://m.weather.com.cn/data/101010100.html");
		layout = new LinearLayout(getActivity());
		return layout;
	}

	class DownTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPostExecute(String result) {
			WeatherData data = new WeatherData(getActivity());
			progress.dismiss();
			if (result == null || result.equals("")) {
				Toast.makeText(getActivity(), "网络加载失败", Toast.LENGTH_SHORT)
						.show();
			} else {
				weather = data.getData(result);
				if (weather == null) {
					Toast.makeText(getActivity(), "数据解析失败", Toast.LENGTH_SHORT)
							.show();
				} else {
					List<Integer> minList = weather.getMinlist();
					List<Integer> maxList = weather.getMaxlist();
					int[] colors = new int[] { Color.RED };
					XYMultipleSeriesDataset dataset = buildDataset(minList,
							maxList);
					XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
					setChartSettings(renderer, "未来六天的温度", "第几天", "温度℃", 0, 7,
							-10, 20, Color.GRAY, Color.LTGRAY);
					renderer.setBarSpacing(0.5);
					renderer.setYLabels(10);
					renderer.addYTextLabel(-25, "Very cold");
					renderer.addYTextLabel(-10, "Cold");
					renderer.addYTextLabel(5, "OK");
					renderer.addYTextLabel(20, "Nice");
					renderer.setMargins(new int[] { 30, 70, 10, 0 });
					renderer.setYLabelsAlign(Align.RIGHT);
					SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
					r.setDisplayChartValues(true);
					r.setChartValuesTextSize(12);
					r.setChartValuesSpacing(3);
					r.setGradientEnabled(true);
					r.setGradientStart(-20, Color.BLUE);
					r.setGradientStop(20, Color.GREEN);
					renderer.setAxisTitleTextSize(16);
					renderer.setChartTitleTextSize(20);
					renderer.setLabelsTextSize(30);
					renderer.setLegendTextSize(30);
					View chart = ChartFactory.getRangeBarChartView(
							getActivity(), dataset, renderer, Type.DEFAULT);
					layout.addView(chart);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(getActivity());
			// 设置对话框的标题
			progress.setTitle("任务正在执行中");
			// 设置对话框的内容
			progress.setMessage("正在加载，请稍后》》》》》");
			progress.setCancelable(true);
			progress.setProgressStyle(getActivity().TRIM_MEMORY_COMPLETE);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// 获取HttpGet对象
			HttpGet httpRequest = new HttpGet(params[0]);
			String strResult = "";
			try {
				// HttpClient对象
				HttpClient httpClient = HttpHelper.getHttpClient();
				// 获得HttpResponse对象
				HttpResponse httpResponse = httpClient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// 取得返回的数据
					strResult = EntityUtils.toString(httpResponse.getEntity());
					return strResult; // 返回结果
				} 
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				try {
					throw AppException.http(e);
				} catch (AppException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					throw AppException.network(e);
				} catch (AppException e1) {
					e1.printStackTrace();
				}
			}
			return null;
		}
	}

	protected XYMultipleSeriesDataset buildDataset(List<Integer> minValues,
			List<Integer> maxValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		RangeCategorySeries series = new RangeCategorySeries("Temperature");
		int length = minValues.size();
		for (int k = 0; k < length; k++) {
			series.add(minValues.get(k), maxValues.get(k));
		}
		dataset.addSeries(series.toXYSeries());
		return dataset;
	}

	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
			r1.setColor(colors[i]);
			renderer.addSeriesRenderer(r1);
		}
		return renderer;
	}

	/**
	 * @param renderer
	 * @param title
	 * @param xTitle
	 * @param yTitle
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param axesColor
	 * @param labelsColor
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
