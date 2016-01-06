package com.kodelabs.mycosts.presentation.ui.customviews;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kodelabs.mycosts.R;
import com.kodelabs.mycosts.domain.model.Cost;
import com.kodelabs.mycosts.presentation.model.DailyTotalCost;
import com.kodelabs.mycosts.utils.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by dmilicic on 1/6/16.
 */
public class ExpandedCostView extends CardView {

    private Context mContext;

    @Bind(R.id.cost_item_title)
    TextView mTitle;

    @Bind(R.id.cost_item_total_value)
    TextView mValue;

    @Bind(R.id.layout_individual_cost_items)
    LinearLayout mLinearLayout;

    public ExpandedCostView(Context context) {
        super(context);
        init(context);
    }

    public ExpandedCostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandedCostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.expanded_cost_item, this);

        ButterKnife.bind(this, view);
    }

    private void addCostItem(Cost cost, int position) {
        CostItemView costView = new CostItemView(mContext);
        costView.setCost(cost);

        // every other cost item will have a different background so its easier on the eyes
        if (position % 2 == 0) {
            costView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBrightGray));
        }

        mLinearLayout.addView(costView);
    }

    private void setTitle(Date date) {
        final String dateText = DateUtils.dateToText(mContext, date);
        final String title = String.format(mContext.getString(R.string.total_expenses), dateText);
        mTitle.setText(title);
    }

    private void setTotalValue(double value) {
        String val = String.valueOf(value);
        mValue.setText(val);
    }

    public void setDailyCost(DailyTotalCost dailyCost) {

        // reset the individual cost items
        mLinearLayout.removeAllViews();

        // convert date to text
        setTitle(dailyCost.getDate());

        setTotalValue(dailyCost.getTotalCost());

        List<Cost> costList = dailyCost.getCostList();
        Cost cost;

        Timber.w("ADDING COST LIST");

        for (int idx = 0; idx < costList.size(); idx++) {
            cost = costList.get(idx);
            addCostItem(cost, idx);

            Timber.w(cost.toString());
        }
    }
}
