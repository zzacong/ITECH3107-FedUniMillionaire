package au.edu.federation.itech3107.fedunimillionaire30360914.helpers.interfaces;

import au.edu.federation.itech3107.fedunimillionaire30360914.helpers.QuestionBank;

public interface OnQuestionsReadyCallback {
    /**
     * reference: https://stackoverflow.com/a/35629470
     */
    void onQuestionsReady(QuestionBank questionBank);
}
