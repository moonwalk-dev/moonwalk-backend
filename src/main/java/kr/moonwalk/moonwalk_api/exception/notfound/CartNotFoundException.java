package kr.moonwalk.moonwalk_api.exception.notfound;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}

