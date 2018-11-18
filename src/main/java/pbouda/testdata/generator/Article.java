package pbouda.testdata.generator;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.UUID;

public class Article {

    final int articleId;
    final String authorId;
    final String authorEmail;
    final String authorName;
    final String title;
    final String url;
    final String city;
    final String country;
    final String content;

    private static final Lorem LOREM = LoremIpsum.getInstance();

    static Article create(int id) {
        return new Article(
                id,
                UUID.randomUUID().toString(),
                LOREM.getEmail(),
                LOREM.getName(),
                LOREM.getTitle(3, 50),
                LOREM.getUrl(),
                LOREM.getCity(),
                LOREM.getCountry(),
                LOREM.getHtmlParagraphs(5, 100));
    }

    private Article(
            int articleId,
            String authorId,
            String authorEmail,
            String authorName,
            String title,
            String url,
            String city,
            String country,
            String content) {

        this.articleId = articleId;
        this.authorId = authorId;
        this.authorEmail = authorEmail;
        this.authorName = authorName;
        this.title = title;
        this.url = url;
        this.city = city;
        this.country = country;
        this.content = content;
    }
}