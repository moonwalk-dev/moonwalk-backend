package kr.moonwalk.moonwalk_api.exception.notfound;

public class ProjectModuleNotFoundException extends RuntimeException{
    public ProjectModuleNotFoundException(String message) {
        super(message);
    }
}
