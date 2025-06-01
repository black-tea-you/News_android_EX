package kr.h.gachon.news_application.network.model;

public class SearchResult {

    private News news;
    private float score;

    // 기본 생성자 (필수)
    public SearchResult() {
    }

    // 모든 필드를 받는 생성자 (선택 사항)
    public SearchResult(News news, float score) {
        this.news = news;
        this.score = score;
    }

    // ======== Getter / Setter ========
    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SearchResultDTO{" +
                "news=" + news +
                ", score=" + score +
                '}';
    }
}