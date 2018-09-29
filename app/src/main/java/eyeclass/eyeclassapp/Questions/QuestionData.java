package eyeclass.eyeclassapp.Questions;

import java.util.ArrayList;
import java.util.List;

public class QuestionData {
    private String id;
    private String question;
    private String rightAns;
    private List<String> wrongOptions;
    private String topic;
    private int time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRightAns() {
        return rightAns;
    }

    public void setRightAns(String rightAns) {
        this.rightAns = rightAns;
    }

    public List<String> getAllOptions() {
        List<String> options = new ArrayList<>(wrongOptions);
        options.add(rightAns);
        return options;
    }

    public void setWrongOptions(List<String> allOptions) {
        this.wrongOptions = allOptions;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


}
