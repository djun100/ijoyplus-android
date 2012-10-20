package sqq.ScrollViewPull;

import sqq.ScrollViewPull.widget.TryRefreshableView;
import sqq.ScrollViewPull.widget.TryRefreshableView.RefreshListener;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 
 * @author sqq
 *
 */
public class ScrollViewPull extends Activity {
	private ScrollView sv;
	private String s;
	private TryRefreshableView rv;
	private String[] mStrings = { "aaaaaaaaaaaaaaaaaa", "bb" };
	private TextView msvTextView;
	String msg = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pull_to_refresh_tryscroll);
		sv = (ScrollView) findViewById(R.id.trymySv);
		rv = (TryRefreshableView) findViewById(R.id.trymyRV);
		rv.mfooterView = (View) findViewById(R.id.tryrefresh_footer);
		rv.sv = sv;
		
		//����mfooterView
		rv.mfooterViewText = (TextView) findViewById(R.id.tryrefresh_footer_text);
		
		
		
		s = "Android��һ����LinuxΪ�����Ŀ���Դ�����ϵͳ����Ҫʹ���ڱ�Я�豸��Ŀǰ��δ��ͳһ�������ƣ��й���½�����϶���ʹ�ð�׿���ǹٷ������£��ٷ�����";

		for (int t = 0; t < 20; t++) {
			msg += s;
		}
		msvTextView = (TextView) findViewById(R.id.sv_text);
		msvTextView.setText(msg);
		Log.i("other","msvTextView.getHeight()"+msvTextView.getHeight());
		
		msvTextView.setTextSize(23);


		
		//�����Ƿ����ˢ��
		rv.setRefreshListener(new RefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if (rv.mRefreshState == 4) {
					new GetHeaderDataTask().execute();
				} else if (rv.mfooterRefreshState == 4) {
					new GetFooterDataTask().execute();
				}

			}
		});
		

	}

	private class GetHeaderDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			msg = mStrings[0] + msg;
			msvTextView.setText(msg);
			msvTextView.setTextSize(23);
			rv.finishRefresh();

			super.onPostExecute(result);
		}

	}

	private class GetFooterDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			msg = msg + mStrings[0];
			msvTextView.setText(msg);
			msvTextView.setTextSize(23);
			rv.finishRefresh();

			super.onPostExecute(result);
		}

	}

}