package com.example.weather;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
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

import android.R.layout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FirstFragment extends Fragment {
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
				Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT)
						.show();
			} else {
				weather = data.getData(result);
				if (weather == null) {
					Toast.makeText(getActivity(), "数据解析失败", Toast.LENGTH_SHORT)
					.show();
				} else {
					String[] titles = new String[] { "最高温度", "最低温度" };
					List x = new ArrayList();
					List y = new ArrayList();
					x.add(new int[] { 0, 1, 2, 3, 4, 5 });
					x.add(new int[] { 0, 1, 2, 3, 4, 5 });
					y.add(weather.getMaxlist());
					y.add(weather.getMinlist());
					XYMultipleSeriesDataset dataset = buildDataset(titles, x, y);
					int[] colors = new int[] { Color.BLUE, Color.GREEN };
					PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
							PointStyle.DIAMOND };
					XYMultipleSeriesRenderer renderer = buildRenderer(colors,
							styles, true);

					setChartSettings(renderer, "未来六天的温度", "第几天", "温度℃", 0, 6,
							-20, 40, Color.BLUE, Color.BLUE);

					View chart = ChartFactory.getLineChartView(getActivity(),
							dataset, renderer);
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
			// HttpClient对象
			HttpClient httpClient = HttpHelper.getHttpClient();
			// 获得HttpResponse对象
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpRequest);

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

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List xValues, List yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; // 有几条线
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i]); // 根据每条线的名称创建
			int[] xV = (int[]) xValues.get(i); // 获取第i条线的数据
			List<Integer> yV = (List<Integer>) yValues.get(i);
			int seriesLength = xV.length; // 有几个点

			for (int k = 0; k < seriesLength; k++) // 每条线里有几个点
			{
				series.add(xV[k], yV.get(k));
			}

			dataset.addSeries(series);
		}

		return dataset;
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));// 设置轴标题文字的大小
		renderer.setChartTitleTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics()));// 设置整个图表标题文字的大小
		renderer.setLabelsTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));// 设置轴刻度文字的大小
		renderer.setLegendTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));// 设置图例文字大小
		renderer.setPointSize(5f);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		renderer.setMargins(new int[] { 50, 30, 15, 20 });// 设置图表的外边框(上/左/下/右)

		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setFillPoints(fill);
			r.setChartValuesTextSize(20);
			r.setChartValuesFormat(new DecimalFormat("#℃"));
			r.setDisplayChartValues(true);
			r.setDisplayChartValuesDistance(10);
			renderer.addSeriesRenderer(r);

		}
		return renderer;
	}

	/**
	 * 设置renderer的一些属性.
	 * 
	 * @param renderer
	 *            要设置的renderer
	 * @param title
	 *            图表标题
	 * @param xTitle
	 *            X轴标题
	 * @param yTitle
	 *            Y轴标题
	 * @param xMin
	 *            X轴最小值
	 * @param xMax
	 *            X轴最大值
	 * @param yMin
	 *            Y轴最小值
	 * @param yMax
	 *            Y轴最大值
	 * @param axesColor轴颜色
	 */

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setShowLabels(true);
		renderer.setShowAxes(true);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(0);
		renderer.setBackgroundColor(Color.RED);
		renderer.setXLabels(8);// 设置x轴显示6个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		renderer.setYLabels(0);// 设置y轴显示
		renderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
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
