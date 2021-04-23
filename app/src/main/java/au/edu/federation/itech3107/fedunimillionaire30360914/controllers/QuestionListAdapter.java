package au.edu.federation.itech3107.fedunimillionaire30360914.controllers;

import android.util.Log;
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

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {

    public static final String LOG_TAG = QuestionListAdapter.class.getSimpleName();

    private List<Question> localDataSet;

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
        this.localDataSet = localDataSet;
    }

    public void addItem(Question item) {
        localDataSet.add(item);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteItems() {
        List<Question> questionToDelete = new ArrayList<>();
        for (Question q : localDataSet) {
            if (q.isChecked) {
                questionToDelete.add(q);
                // TODO: Delete from file
            }
        }
        localDataSet.removeAll(questionToDelete);
        notifyDataSetChanged();
    }

    public void refresh(List<Question> dataSet) {
        this.localDataSet = dataSet;
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
        Question question = localDataSet.get(position);

        holder.tvQuestionTitle.setText(question.getTitle());
        holder.tvDifficulty.setText(question.getDifficulty().toString());
        holder.tvCorrectAnswer.setText(question.getChoices().get(question.getAnswer()));

        int j = 0;
        for (int i = 0; i < question.getChoices().size(); i++) {
            if (i == question.getAnswer())
                continue;

            holder.tvWrongAnswers.get(j).setText(question.getChoices().get(i));
            j++;
        }

        holder.cbDelete.setChecked(question.isChecked);
        holder.cbDelete.setOnCheckedChangeListener((cb, isChecked) -> {
            question.isChecked = isChecked;
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}