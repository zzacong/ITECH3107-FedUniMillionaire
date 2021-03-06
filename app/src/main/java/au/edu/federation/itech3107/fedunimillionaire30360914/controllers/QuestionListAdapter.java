package au.edu.federation.itech3107.fedunimillionaire30360914.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30360914.R;
import au.edu.federation.itech3107.fedunimillionaire30360914.models.Question;

/**
 * Use of RecyclerView adapter with checkboxes | Referenced from https://www.youtube.com/watch?v=BBWyXo-3JGQ
 */
public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {

    public static final String LOG_TAG = QuestionListAdapter.class.getSimpleName();

    private List<Question> mLocalDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvQuestionTitle, tvDifficulty, tvCorrectAnswer;
        final List<TextView> tvWrongAnswers = new ArrayList<>();
        final CheckBox cbDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionTitle = itemView.findViewById(R.id.tvQuestionTitle);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);

            tvWrongAnswers.add(itemView.findViewById(R.id.tvWrongAnswer1));
            tvWrongAnswers.add(itemView.findViewById(R.id.tvWrongAnswer2));
            tvWrongAnswers.add(itemView.findViewById(R.id.tvWrongAnswer3));
            cbDelete = itemView.findViewById(R.id.cbDeleteQuestion);
        }
    }


    public QuestionListAdapter(List<Question> localDataSet) {
        this.mLocalDataSet = localDataSet;
    }


    public List<Question> getDataSet() {
        return mLocalDataSet;
    }

    public void addItem(Question item) {
        mLocalDataSet.add(item);
        notifyItemInserted(mLocalDataSet.size() - 1);
    }

    public void refresh(List<Question> dataSet) {
        this.mLocalDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QuestionListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionListAdapter.ViewHolder holder, int position) {
        Question question = mLocalDataSet.get(position);

        holder.tvQuestionTitle.setText(question.getTitle());
        holder.tvDifficulty.setText(question.getDifficulty().toString());
        holder.tvCorrectAnswer.setText(question.getChoices().get(question.getAnswer()));

        int j = 0;
        for (int i = 0; i < question.getChoices().size(); i++) {
            if (i == question.getAnswer()) continue;

            holder.tvWrongAnswers.get(j).setText(question.getChoices().get(i));
            j++;
        }

        holder.cbDelete.setChecked(question.isChecked());
        holder.cbDelete.setOnCheckedChangeListener((cb, isChecked) -> {
            question.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return mLocalDataSet.size();
    }
}