package com.storycraft.speech.enums;

public enum VoiceId {
    // 한국어
    Seoyeon("ko"),
    // 영어
    Joanna("en"), Matthew("en"), Ruth("en"), Stephen("en");

    private final String lang;
    VoiceId(String lang){ this.lang = lang; }
    public String lang(){ return lang; }
}
