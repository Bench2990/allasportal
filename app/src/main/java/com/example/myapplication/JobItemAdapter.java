package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<JobItem> mJobItems = new ArrayList<>();
    private ArrayList<JobItem> mJobItemsAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    public JobItemAdapter(Context context, ArrayList<JobItem> jobItems) {
        this.mContext = context;
        this.mJobItems = jobItems;
        this.mJobItemsAll = jobItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_job, parent, false));
    }

    @Override
    public void onBindViewHolder(JobItemAdapter.ViewHolder holder, int position) {
        JobItem currentItem = mJobItems.get(position);
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mJobItems.size();
    }

    @Override
    public Filter getFilter() {
        return jobFilter;
    }

    private final Filter jobFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<JobItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mJobItemsAll);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (JobItem item : mJobItemsAll) {
                    if (item.getTitle().toLowerCase().contains(filterPattern) ||
                            item.getCompany().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mJobItems.clear();
            mJobItems.addAll((ArrayList<JobItem>) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView jobTitleText;
        private TextView companyText;
        private TextView locationText;
        private TextView salaryText;
        private TextView workTypeText;
        private Button applyButton;

        public ViewHolder(View itemView) {
            super(itemView);
            jobTitleText = itemView.findViewById(R.id.jobTitle);
            companyText = itemView.findViewById(R.id.companyName);
            locationText = itemView.findViewById(R.id.location);
            salaryText = itemView.findViewById(R.id.salary);
            workTypeText = itemView.findViewById(R.id.workType);
            applyButton = itemView.findViewById(R.id.applyButton);

            applyButton.setOnClickListener(view -> {

            });
        }

        void bindTo(JobItem jobItem) {
            jobTitleText.setText(jobItem.getTitle());
            companyText.setText(jobItem.getCompany());
            locationText.setText(jobItem.getLocation());
            salaryText.setText(jobItem.getSalary());
            workTypeText.setText(jobItem.getWorkType());
        }
    }
}
