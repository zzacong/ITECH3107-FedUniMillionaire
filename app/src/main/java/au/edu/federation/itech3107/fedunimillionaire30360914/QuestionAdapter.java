package au.edu.federation.itech3107.fedunimillionaire30360914;

import java.util.ArrayList;

public class QuestionAdapter {

    private final int MAX = 11;
    private final ArrayList<Question> questionList;
    private QuestionBank questionBank;
    private Integer currentNumber = 0;
    private Integer questionsLeft = MAX - currentNumber;


    public QuestionAdapter() {
        this.questionBank = QuestionBank.getInstance();
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
        if (number >= 1 && number <= 5) {
            return questionBank.getSafeMoneyValue(1);
        } else if (number >= 6 && number <= 10) {
            return questionBank.getSafeMoneyValue(2);
        } else if (number >= 11) {
            return questionBank.getSafeMoneyValue(3);
        } else {
            return 0;
        }
    }

    public Integer getQuestionValue() {
        return questionBank.getQuestionValue(this.currentNumber);
    }
}
