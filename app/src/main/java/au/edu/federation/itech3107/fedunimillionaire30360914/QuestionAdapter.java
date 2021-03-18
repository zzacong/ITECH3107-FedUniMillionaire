package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class QuestionAdapter {

    private final ArrayList<Question> questionList;
    private final Iterator<Question> questionIter;

    public QuestionAdapter() {
        this.questionList = QuestionBank.getQuestions();
        this.questionIter = questionList.iterator();
    }

    public Question nextQuestion() {
        if (questionIter.hasNext()) {
            return questionIter.next();
        }
        return null;
    }
}
