package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;

import static au.edu.federation.itech3107.fedunimillionaire30360914.QuestionBank.QUESTION_VALUE_SAFE_MONEY_LIST;

public class QuestionAdapter {

    private final int MAX = 11;
    private final ArrayList<Question> questionList;
    private QuestionBank questionBank;
    private Integer currentNumber = 0;
    private Integer questionsLeft = MAX - currentNumber;


    public QuestionAdapter(QuestionBank questionBank) {
        this.questionBank = questionBank;
        this.questionList = questionBank.getQuestions();
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public Question startFrom(int number) {
        if (number <= 0) this.currentNumber = 0;
        else this.currentNumber = number - 1;
        return nextQuestion();
    }

    public Integer getQuestionsLeft() {
        return questionsLeft;
    }

    public Question nextQuestion() {
        this.currentNumber++;
        if (this.currentNumber <= questionList.size()) {
            this.questionsLeft = MAX - this.currentNumber;
            return questionList.get(this.currentNumber - 1);
        }
        return null;
    }

    public Integer getSafeMoneyValue() {
        int number = this.currentNumber - 1;
        return QUESTION_VALUE_SAFE_MONEY_LIST.get(number)[1];
    }

    public Integer getQuestionValue() {
        return QUESTION_VALUE_SAFE_MONEY_LIST.get(currentNumber)[0];
    }
}
