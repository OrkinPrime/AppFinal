package com.lizhongbin.ch_final;

public class Record {
    private int id;
    private int scores;
    private String playerName;

    public Record(int scores, String playerName) {
        this.scores = scores;
        this.playerName = playerName;
    }

    public Record() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
