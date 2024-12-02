package kr.moonwalk.moonwalk_api.exception.notfound;

public class GuideNotFoundException extends RuntimeException {
    public GuideNotFoundException(String message) {
        super(message);
    }
}