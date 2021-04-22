package au.edu.federation.itech3107.fedunimillionaire30360914.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Score;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ViewHolder> {

    public static final String LOG_TAG = ScoreListAdapter.class.getSimpleName();

    private List<Score> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName, tvMoney, tvDatetime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
            tvDatetime = (TextView) itemView.findViewById(R.id.tvDatetime);
        }
    }

    public ScoreListAdapter(List<Score> localDataSet) {
        Log.d(LOG_TAG, "[ADAPTER]");
        this.localDataSet = localDataSet;
        Log.d(LOG_TAG, this.localDataSet.size() + "");
    }

    public void refresh(List<Score> dataSet) {
        this.localDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScoreListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "[ON CREATE VIEW HOLDER]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreListAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, "[ON BIND VIEW HOLDER]");
        Score score = localDataSet.get(position);
        Log.d(LOG_TAG, score.toString());

        holder.tvName.setText(score.getName());
        String money = "$" + score.getMoney();
        holder.tvMoney.setText(money);
        holder.tvDatetime.setText(score.getDatetime());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
