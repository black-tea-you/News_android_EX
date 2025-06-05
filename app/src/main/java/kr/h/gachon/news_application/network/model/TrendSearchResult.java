package kr.h.gachon.news_application.network.model;

import java.util.List;

public class TrendSearchResult {
    private String           start_date;
    private String           end_date;
    private List<String>     keywords;
    private List<TrendSearchDTO> news;

    public TrendSearchResult () { }

    public TrendSearchResult (
            String start_date,
            String end_date,
            List<String> keywords,
            List<TrendSearchDTO> news
    ) {
        this.start_date = start_date;
        this.end_date   = end_date;
        this.keywords   = keywords;
        this.news       = news;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<TrendSearchDTO> getNews() {
        return news;
    }

    public void setNews(List<TrendSearchDTO> news) {
        this.news = news;
    }
}