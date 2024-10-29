package sample.test.dto;

public class NotificationRequestDto {
    private String title;
    private String body;
    private String image;

    public NotificationRequestDto(String title, String body, String image) {
        this.title = title;
        this.body = body;
        this.image = image;
    }
}
