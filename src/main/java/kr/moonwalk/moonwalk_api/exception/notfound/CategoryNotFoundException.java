package kr.moonwalk.moonwalk_api.exception.notfound;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}

