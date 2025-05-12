package kr.h.gachon.news_application.network.model;

public class News {
        private Long id;
        private String title;
        private String description;
        private String summary;
        private String link;
        private String date;
        private String urlimg;
        private String category;
        private String keyword;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getUrlimg() { return urlimg; }
        public void setUrlimg(String urlimg) { this.urlimg = urlimg; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
}

