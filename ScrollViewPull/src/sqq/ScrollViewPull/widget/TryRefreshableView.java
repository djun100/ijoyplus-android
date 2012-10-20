package sqq.ScrollViewPull.widget;

import sqq.ScrollViewPull.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
/**
 * 
 * @author sqq  
 * @remark �ο�������Nono�Ĵ���
 *
 */
public class TryRefreshableView extends LinearLayout {

	private static final int TAP_TO_REFRESH = 1; // ��ʼ״̬
	private static final int PULL_TO_REFRESH = 2; // ����ˢ��
	private static final int RELEASE_TO_REFRESH = 3; // �ͷ�ˢ��
	private static final int REFRESHING = 4; // ����ˢ��
	public int mRefreshState;// ��¼ͷ��ǰ״̬
	public int mfooterRefreshState;//��¼β��ǰ״̬
	public Scroller scroller;
	public ScrollView sv;
	private View refreshView;//ͷ����ͼ
	public View mfooterView;// β����ͼ
	public TextView mfooterViewText;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -60;
	public int refreshFooter;
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;

	private RefreshListener refreshListener;

	private int lastY;
	// ����Ч��
	// ��Ϊ���µļ�ͷ
	private RotateAnimation mFlipAnimation;
	// ��Ϊ����ļ�ͷ
	private RotateAnimation mReverseFlipAnimation;
	public int nowpull = -1;// 0Ϊͷ��������1Ϊβ������

	private boolean isRecord;
	private Context mContext;

	public TryRefreshableView(Context context) {
		super(context);
		mContext = context;

	}

	public TryRefreshableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		// ��ʼ������
		//
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);

		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);
		// ��������
		scroller = new Scroller(mContext);

		// ˢ����ͼ���˵ĵ�view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.pull_to_refresh_header, null);
		//��ͷͼ��
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.pull_to_refresh_image);
		// ˢ��bar
		bar = (ProgressBar) refreshView
				.findViewById(R.id.pull_to_refresh_progress);
		// ������ʾtext
		downTextView = (TextView) refreshView
				.findViewById(R.id.pull_to_refresh_text);
		// ������ʾʱ��
		timeTextView = (TextView) refreshView
				.findViewById(R.id.pull_to_refresh_updated_at);
		
		//���ͷ��view
		refreshView.setMinimumHeight(50);
		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		
		isRecord = false;

		mRefreshState = TAP_TO_REFRESH;
		mfooterRefreshState = TAP_TO_REFRESH;
		

	}

	public boolean onTouchEvent(MotionEvent event) {

		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ��¼��y����
			if (isRecord == false) {
				Log.i("moveY", "lastY:" + y);
				lastY = y;
				isRecord = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:

			Log.i("TAG", "ACTION_MOVE");
			// y�ƶ�����
			Log.i("moveY", "lastY:" + lastY);
			Log.i("moveY", "y:" + y);
			int m = y - lastY;

			doMovement(m);
			// ��¼�´˿�y����
			lastY = y;

			
			
			break;

		case MotionEvent.ACTION_UP:
			Log.i("TAG", "ACTION_UP");

			fling();

			isRecord = false;
			break;
		}
		return true;
	}

	/**
	 * up�¼�����
	 */
	private void fling() {
		// TODO Auto-generated method stub
		if (nowpull == 0 && mRefreshState != REFRESHING) {
			LinearLayout.LayoutParams lp = (LayoutParams) refreshView
					.getLayoutParams();
			Log.i("TAG", "fling()" + lp.topMargin);
			if (lp.topMargin > 0) {// �����˴�����ˢ���¼�
				refresh();
			} else {
				returnInitState();
			}
		} else if (nowpull == 1 && mfooterRefreshState != REFRESHING) {
		

			if (refreshFooter >= 20
					&& mfooterRefreshState == RELEASE_TO_REFRESH) {
				mfooterRefreshState = REFRESHING;
				FooterPrepareForRefresh(); // ׼��ˢ��
				onRefresh(); // ˢ��
			} else {
				if (refreshFooter>=0)
					resetFooterPadding();
				else {
					resetFooterPadding();
					mfooterRefreshState = TAP_TO_REFRESH;
					Log.i("other","i::"+refreshFooter);
					TryPullToRefreshScrollView.ScrollToPoint(sv, sv.getChildAt(0),-refreshFooter);
				}
			}
		}
	}

	// ˢ��
	public void onRefresh() {
		Log.d("TAG", "ִ��ˢ��");

		if (refreshListener != null) {

			refreshListener.onRefresh();
		}
	}

	private void returnInitState() {
		// TODO Auto-generated method stub
		mRefreshState = TAP_TO_REFRESH;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
	}

	private void refresh() {
		// TODO Auto-generated method stub
		mRefreshState = REFRESHING;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.GONE);
		refreshIndicatorView.setImageDrawable(null);
		bar.setVisibility(View.VISIBLE);
		timeTextView.setVisibility(View.GONE);
		downTextView.setText(R.string.pull_to_refresh_refreshing_label);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();

		if (refreshListener != null) {

			refreshListener.onRefresh();
		}

	}

	private void resetFooterPadding() {
		LayoutParams svlp = (LayoutParams) sv.getLayoutParams();
		svlp.bottomMargin=0;
		
		sv.setLayoutParams(svlp);
		TryPullToRefreshScrollView.ScrollToPoint(sv, sv.getChildAt(0),0);

	}

	public void FooterPrepareForRefresh() {
		resetFooterPadding();
		mfooterViewText
				.setText(R.string.pull_to_refresh_footer_refreshing_label);
		mfooterRefreshState = REFRESHING;
	}

	/**
	 * 
	 */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset()) {
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int k = Math.max(i, refreshTargetTop);
			lp.topMargin = k;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}

	/**
	 * ����move�¼�����
	 * 
	 * @param moveY
	 */
	public void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
		.getLayoutParams();

		if(sv.getScrollY() == 0 && moveY > 0&&refreshFooter<=0)
		{
			nowpull=0;
		}
		if(sv.getChildAt(0).getMeasuredHeight() <= sv.getScrollY() + getHeight() && moveY < 0&&lp.topMargin<=refreshTargetTop)
		{
			nowpull=1;
		}
		
		if (nowpull == 0 && mRefreshState != REFRESHING) {
			
			// ��ȡview���ϱ߾�
			float f1 = lp.topMargin;
			float f2 = f1 + moveY * 0.3F;
			int i = (int) f2;
			// �޸��ϱ߾�
			lp.topMargin = i;
			// �޸ĺ�ˢ��
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();

			
			downTextView.setVisibility(View.VISIBLE);

			refreshIndicatorView.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			if (lp.topMargin > 0 && mRefreshState != RELEASE_TO_REFRESH) {
				downTextView.setText(R.string.refresh_release_text);
				// refreshIndicatorView.setImageResource(R.drawable.goicon);
				refreshIndicatorView.clearAnimation();
				refreshIndicatorView.startAnimation(mFlipAnimation);
				mRefreshState = RELEASE_TO_REFRESH;

				Log.i("TAG", "���ڴ�������״̬");
			} else if (lp.topMargin <= 0 && mRefreshState != PULL_TO_REFRESH) {
				downTextView.setText(R.string.refresh_down_text);
				// refreshIndicatorView.setImageResource(R.drawable.goicon);
				if (mRefreshState != TAP_TO_REFRESH) {
					refreshIndicatorView.clearAnimation();
					refreshIndicatorView.startAnimation(mReverseFlipAnimation);

					Log.i("TAG", "���ڴ��ڻص�״̬");

				}
				mRefreshState = PULL_TO_REFRESH;

			}
		} 
		else if (nowpull == 1 && mfooterRefreshState != REFRESHING) {

			LayoutParams svlp = (LayoutParams) sv.getLayoutParams();
			svlp.bottomMargin=svlp.bottomMargin-moveY;
			Log.i("other","svlp.bottomMargin::"+svlp.bottomMargin);
			refreshFooter=svlp.bottomMargin;
			sv.setLayoutParams(svlp);
			TryPullToRefreshScrollView.ScrollToPoint(sv, sv.getChildAt(0),0);
			
			if (svlp.bottomMargin >= 20
					&& mfooterRefreshState != RELEASE_TO_REFRESH) {
				mfooterViewText.setText(R.string.pull_to_refresh_footer_label);
				mfooterRefreshState = RELEASE_TO_REFRESH;
			} else if (svlp.bottomMargin < 20
					&& mfooterRefreshState != PULL_TO_REFRESH) {
				mfooterViewText
						.setText(R.string.pull_to_refresh_footer_pull_label);
				mfooterRefreshState = PULL_TO_REFRESH;
			}
			
		}

	}

	
	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	
	/**
	 * ����ˢ���¼�
	 */
	public void finishRefresh() {
		Log.i("TAG", "ִ����=====finishRefresh");

		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH; // ��ʼˢ��״̬
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int i = lp.topMargin;
			refreshIndicatorView.setImageResource(R.drawable.goicon);
			refreshIndicatorView.clearAnimation();
			bar.setVisibility(View.GONE);
			refreshIndicatorView.setVisibility(View.GONE);
			downTextView.setText(R.string.pull_to_refresh_tap_label);
			scroller.startScroll(0, i, 0, refreshTargetTop);
			invalidate();
		}
		if (mfooterRefreshState != TAP_TO_REFRESH) {
			resetFooter();
		}
	}

	public void resetFooter() {

		mfooterRefreshState = TAP_TO_REFRESH; // ��ʼˢ��״̬
		// ʹͷ����ͼ�� toppadding �ָ�����ʼֵ
		resetFooterPadding();
		// Set refresh view text to the pull label
		// �����ֳ�ʼ��
		mfooterViewText.setText(R.string.pull_to_refresh_footer_pull_label);

	}

	public void HideFooter() {
		LayoutParams mfvlp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mfvlp.bottomMargin = refreshFooter;
		mfooterView.setLayoutParams(mfvlp);
		mfooterRefreshState = TAP_TO_REFRESH;
	}

	
	/*
	 * �÷���һ���ontouchEvent һ����
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// lastY = y;
			if (isRecord == false) {
				Log.i("moveY", "lastY:" + y);
				lastY = y;
				isRecord = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			// y�ƶ�����
			int m = y - lastY;

			if (canScroll(m)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			isRecord = false;
			break;

		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return false;
	}

	private boolean canScroll(int diff) {
		// TODO Auto-generated method stub
		View childView;
		Log.i("other", "mRefreshState:" + mRefreshState);
		if (mRefreshState == REFRESHING || mfooterRefreshState == REFRESHING) {
			return true;
		}
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
			
				// ͷ������
				if (((ScrollView) childView).getScrollY() == 0 && diff > 0) {
					nowpull = 0;
					Log.i("other", "�����");
					return true;
				} else if ((((ScrollView) childView).getChildAt(0)
						.getMeasuredHeight() <= ((ScrollView) childView)
						.getScrollY() + getHeight() && diff < 0)) {// �ײ�����
					Log.i("other", "�����2");
					nowpull = 1;
					
					return true;
				} else {
					Log.i("other", "ScrollView����");
					return false;
				}
			}

		}
		return false;
	}

	/**
	 * ˢ�¼����ӿ�
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener {
		public void onRefresh();
	}
}
