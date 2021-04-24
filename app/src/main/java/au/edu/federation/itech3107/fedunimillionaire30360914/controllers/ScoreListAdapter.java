package au.edu.federation.itech3107.fedunimillionaire30360914.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Score;

/**
 * Use of RecyclerView adapter with checkboxes | Referenced from https://www.youtube.com/watch?v=BBWyXo-3JGQ
 */
public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ViewHolder> {

    public static final String LOG_TAG = ScoreListAdapter.class.getSimpleName();

    private List<Score> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName, tvMoney, tvDatetime;
        final CheckBox cbDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            tvDatetime = itemView.findViewById(R.id.tvDatetime);
            cbDelete = itemView.findViewById(R.id.cbDeleteScore);
        }
    }

    public ScoreListAdapter(List<Score> localDataSet) {
        this.localDataSet = localDataSet;
        Log.d(LOG_TAG, this.localDataSet.size() + "");
    }

    public List<Score> getDataSet() {
        return localDataSet;
    }

    public void refresh(List<Score> dataSet) {
        this.localDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScoreListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreListAdapter.ViewHolder holder, int position) {
        Score score = localDataSet.get(position);
        Log.d(LOG_TAG, score.toString());

        holder.tvName.setText(score.getName());
        String money = "$" + score.getMoney();
        holder.tvMoney.setText(money);
        holder.tvDatetime.setText(score.getDatetime());
        holder.cbDelete.setChecked(score.isChecked());

        holder.cbDelete.setOnCheckedChangeListener((cb, isChecked) -> {
            score.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
