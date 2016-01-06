package com.kodelabs.mycosts.presentation.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kodelabs.mycosts.R;
import com.kodelabs.mycosts.domain.model.Cost;
import com.kodelabs.mycosts.presentation.model.DailyTotalCost;
import com.kodelabs.mycosts.presentation.presenters.MainPresenter;
import com.kodelabs.mycosts.presentation.ui.customviews.ExpandedCostView;
import com.kodelabs.mycosts.presentation.ui.listeners.CostViewClickListener;
import com.kodelabs.mycosts.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dmilicic on 12/13/15.
 */
public class CostItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CostViewClickListener {


    private enum ViewType {
        CONTRACTED_CARD, EXPANDED_CARD
    }

    private List<DailyTotalCost> mCostList;
    private Context              mContext;

    private Set<Integer> mSelectedItems;

    public final MainPresenter.View mView;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.cost_item_title)
        public TextView mTitle;

        @Bind(R.id.cost_item_total_value)
        public TextView mTotalCost;

        private CostViewClickListener mListener;

        public void setup(DailyTotalCost dailyTotalCost) {
            Context context = mTitle.getContext();

            final String dateText = DateUtils.dateToText(context, dailyTotalCost.getDate());
            final String title = String.format(context.getString(R.string.total_expenses), dateText);
            mTitle.setText(title);
            mTotalCost.setText(String.valueOf(dailyTotalCost.getTotalCost()) + "$");
        }

        @Override
        public void onClick(View v) {
            mListener.onClickView(getAdapterPosition());
        }

        public ViewHolder(View v, final CostViewClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
            mListener = listener;
        }
    }

    public static class ExpandedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.card_expanded_costview)
        public ExpandedCostView mExpandedCostView;

        private CostViewClickListener mListener;

        @Override
        public void onClick(View v) {
            mListener.onClickView(getAdapterPosition());
        }

        public ExpandedViewHolder(View v, final CostViewClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
            mListener = listener;
        }
    }


    public CostItemAdapter(MainPresenter.View view, Context context) {
        mCostList = new ArrayList<>();
        mView = view;
        mContext = context;
        mSelectedItems = new HashSet<>();
    }

    @Override
    public int getItemViewType(int position) {
        // check to see if a view at this position should be expanded or normal/contracted
        if (mSelectedItems.contains(position))
            return ViewType.EXPANDED_CARD.ordinal();

        return ViewType.CONTRACTED_CARD.ordinal();
    }

    @Override
    public void onClickView(int position) {

        // If clicked on for the first time the view should be counted as selected, if clicked again the view
        // should be considered unselected.
        // Selected views will be shown as expanded cards while unselected will be shown as normal/contracted cards
        if (!mSelectedItems.contains(position))
            mSelectedItems.add(position);
        else
            mSelectedItems.remove(position);

        notifyItemChanged(position);
    }

    @Override
    public void onClickDelete(int position) {
//        Cost cost = mCostList.get(position);
//        mView.onClickDeleteCost(cost);
    }

    @Override
    public void onClickEdit(int position) {
//        Cost cost = mCostList.get(position);
//        mView.onClickEditCost(cost, position);
    }

    public void addNewCosts(@NonNull List<DailyTotalCost> costList) {
        mCostList = costList;
        this.notifyDataSetChanged();
    }

    public void deleteCost(@NonNull Cost cost) {
        int position = mCostList.indexOf(cost);
        mCostList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCostList.size());
    }

    public void onCostUpdated(@NonNull DailyTotalCost cost, int position) {
        mCostList.remove(position);
        mCostList.add(position, cost);
        notifyItemChanged(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // check if this should be an expanded card
        if (viewType == ViewType.EXPANDED_CARD.ordinal()) {
            View view = inflater.inflate(R.layout.card_expanded_daily_cost_item, parent, false);
            return new ExpandedViewHolder(view, this);
        }

        // this is a normal/contracted card
        View view = inflater.inflate(R.layout.card_daily_cost_item, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        DailyTotalCost cost = mCostList.get(position);

        // setup the views depending on the viewholder type
        if (viewHolder instanceof ViewHolder) {
            ((ViewHolder) viewHolder).setup(cost);
        } else if (viewHolder instanceof ExpandedViewHolder) {
            ((ExpandedViewHolder) viewHolder).mExpandedCostView.setDailyCost(cost);
        }
    }

    @Override
    public int getItemCount() {
        return mCostList.size();
    }
}
