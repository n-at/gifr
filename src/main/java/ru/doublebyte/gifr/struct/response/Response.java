package ru.doublebyte.gifr.struct.response;

public class Response {

    private boolean success;
    private String message;

    ///////////////////////////////////////////////////////////////////////////

    public Response() {

    }

    public static Response success() {
        var response = new Response();
        response.success = true;
        response.message = "ok";
        return response;
    }

    public static Response success(String message) {
        var response = new Response();
        response.success = true;
        response.message = message;
        return response;
    }

    public static Response error(String message) {
        var response = new Response();
        response.success = false;
        response.message = message;
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
