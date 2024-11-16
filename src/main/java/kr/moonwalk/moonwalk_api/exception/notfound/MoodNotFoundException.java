package kr.moonwalk.moonwalk_api.exception.notfound;

public class MoodNotFoundException extends RuntimeException {
    public MoodNotFoundException(String message) {
        super(message);
    }
}