package com.bantads.auth.exception;

public class HttpException extends Exception {
    
    private int statusCode;
    
    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static HttpException wrap(int statusCode, String message) {
        return switch (statusCode) {
            case 400 -> new BadRequestException(message);
            case 500 -> new InternalServerErrorException(message);
            case 404 -> new NotFoundException(message);
            case 401 -> new UnauthorizedException(message);
            default -> new HttpException(statusCode, message);
        };
    }
    
}
