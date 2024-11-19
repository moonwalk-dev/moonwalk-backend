package kr.moonwalk.moonwalk_api.exception.notfound;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
}

