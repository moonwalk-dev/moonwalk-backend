package kr.moonwalk.moonwalk_api.exception.notfound;

public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String message) {
        super(message);
    }
}