/**
 * 
 */
package com.example.weather;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.example.common.AppException;
import com.example.common.HttpHelper;
import com.example.entiy.Weather;

import android.R.integer;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author Think
 * 
 */
public class ThridFragment extends Fragment {
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
					List<Integer> values = weather.getMaxlist();
					int[] colors = new int[] { Color.BLUE, Color.GREEN,
							Color.MAGENTA, Color.YELLOW, Color.CYAN ,Color.RED};
					DefaultRenderer renderer = buildCategoryRenderer(colors);
					renderer.setZoomButtonsVisible(true);
					renderer.setZoomEnabled(true);
					renderer.setChartTitleTextSize(20);
					renderer.setShowLabels(true);
					renderer.setChartTitle("未来六天最高温度的饼图");
					renderer.setDisplayValues(true);
					SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
					r.setGradientEnabled(true);
					r.setGradientStart(0, Color.BLUE);//设置渐变色
					r.setGradientStop(0, Color.GREEN);
					r.setHighlighted(true);
					View view = ChartFactory.getPieChartView(getActivity(), buildCategoryDataset("最高温度饼图", values), renderer);
					layout.addView(view);
				}
			}
		}

		protected DefaultRenderer buildCategoryRenderer(int[] colors) {
			DefaultRenderer renderer = new DefaultRenderer();
			renderer.setLabelsTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics()));//设置标签文字大小
			renderer.setLegendTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));//设置图例文字大小
			renderer.setMargins(new int[] { 20, 30, 15, 0 });
			for (int color : colors) {
				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				r.setColor(color);
				renderer.addSeriesRenderer(r);
			}
			return renderer;
		}

		protected CategorySeries buildCategoryDataset(String title,
				List<Integer> values) {
			CategorySeries series = new CategorySeries(title);
			for (int k=0;k<values.size();k++) {
				series.add("第 " + (k+1)+"天", values.get(k));
			}

			return series;
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
