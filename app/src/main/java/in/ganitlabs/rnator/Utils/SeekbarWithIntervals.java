package in.ganitlabs.rnator.Utils;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import in.ganitlabs.rnator.R;

public class SeekbarWithIntervals extends RelativeLayout {
    private RelativeLayout relativeLayout = null;
    private SeekBar seekbar = null;

    public SeekbarWithIntervals(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    private int WidthMeasureSpec = 0;
    private int HeightMeasureSpec = 0;

    @Override
    protected synchronized void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)    {
        WidthMeasureSpec = widthMeasureSpec;
        HeightMeasureSpec = heightMeasureSpec;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            alignIntervals();

            // We've changed the intervals layout, we need to refresh.
            relativeLayout.measure(WidthMeasureSpec, HeightMeasureSpec);
            relativeLayout.layout(relativeLayout.getLeft(), relativeLayout.getTop(),
                    relativeLayout.getRight(), relativeLayout.getBottom());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getActivity().getLayoutInflater().inflate(R.layout.labelled_seekbar_layout, this);
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    private RelativeLayout getRelativeLayout() {
        if (relativeLayout == null) {
            relativeLayout = (RelativeLayout) findViewById(R.id.intervals);
        }
        return relativeLayout;
    }

    private SeekBar getSeekbar() {
        if (seekbar == null) {
            seekbar = (SeekBar) findViewById(R.id.seekbar);
        }
        return seekbar;
    }

    public void setIntervals(List<String> intervals) {
        displayIntervals(intervals);
        getSeekbar().setMax(intervals.size() - 1);
        alignIntervals();
    }

    private void displayIntervals(List<String> intervals) {

        if (getRelativeLayout().getChildCount() == 0) {
            for (String interval : intervals) {
                TextView textViewInterval = createInterval(interval);
                getRelativeLayout().addView(textViewInterval);
            }
        }
    }

    private TextView createInterval(String interval) {
        View textBoxView = LayoutInflater.from(getContext()).inflate(R.layout.labelled_seekbar_label_layout, null);

        TextView textView = (TextView) textBoxView
                .findViewById(R.id.textViewInterval);

        textView.setId(View.generateViewId());
        textView.setText(interval);

        return textView;
    }

    public int getProgress(){
        return seekbar.getProgress();
    }

    private void alignIntervals(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int padding = getSeekbar().getPaddingStart();
        int seekWidth = width-2*padding;
        int count = getRelativeLayout().getChildCount();
        for (int i=0; i< count; i++){
            TextView tv = (TextView)getRelativeLayout().getChildAt(i);
            int margin = padding + i*seekWidth/(count-1) - tv.getWidth()/2;
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(margin);
            tv.setLayoutParams(lp);
        }
    }
}