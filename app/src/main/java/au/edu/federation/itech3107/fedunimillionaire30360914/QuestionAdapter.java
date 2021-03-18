package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class QuestionAdapter {

    private final int MAX = 11;
    private final ArrayList<Question> questionList;
    private final Iterator<Question> questionIter;
    private Question currQuestion;
    private Integer questionNumber = 1;
    private Integer questionsLeft = MAX - questionNumber;


    public QuestionAdapter() {
        this.questionList = QuestionBank.getInstance().getQuestions();
        this.questionIter = questionList.iterator();
        this.currQuestion = questionIter.next();
    }

    public Question getCurrQuestion() {
        return currQuestion;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public Integer getQuestionsLeft() {
        return questionsLeft;
    }

    public Question nextQuestion() {
        if (questionIter.hasNext()) {
            this.currQuestion = questionIter.next();
            this.questionNumber++;
            this.questionsLeft = MAX - this.questionNumber;
            return currQuestion;
        }
        this.currQuestion = null;
        return null;
    }
}
