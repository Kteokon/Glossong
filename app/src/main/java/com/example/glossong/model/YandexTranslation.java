package com.example.glossong.model;

public class YandexTranslation {
    private String text;
    private String detectedLanguageCode;

    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getDetectedLanguageCode() {
        return this.detectedLanguageCode;
    }
    public void setDetectedLanguageCode(String detectedLanguageCode) {
        this.detectedLanguageCode = detectedLanguageCode;
    }
}
