package com.example.weather;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity {
	private ViewPager pager;
	private TestFragmentAdapter adapter;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn = (Button) this.findViewById(R.id.btn);
		adapter = new TestFragmentAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setDrawingCacheQuality(2);
		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter = new TestFragmentAdapter(getSupportFragmentManager());
				pager = (ViewPager) findViewById(R.id.pager);
				pager.setAdapter(adapter);
				pager.setDrawingCacheQuality(2);
				TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
				indicator.setViewPager(pager);
			}
		});
	}
}
