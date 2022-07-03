package com.efw.apps.ui.exercises;

public class Exercise {
    private String name, speak_text;

    public Exercise(String name, String speak_text) {

        this.name = name;
        this.speak_text = speak_text;
    }

    public String getSpeak_text() {
        return speak_text;
    }

    public void setSpeak_text(String speak_text) {
        this.speak_text = speak_text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
