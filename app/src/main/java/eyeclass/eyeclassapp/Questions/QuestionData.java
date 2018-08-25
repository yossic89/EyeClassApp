package eyeclass.eyeclassapp.Questions;

import java.util.List;

public class QuestionData {
    private String id;
    private String question;
    private String rightAns;
    private List<String> allOptions;
    private String topic;

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
        return allOptions;
    }

    public void setAllOptions(List<String> allOptions) {
        this.allOptions = allOptions;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
