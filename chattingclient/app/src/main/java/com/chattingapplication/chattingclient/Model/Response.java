package com.chattingapplication.chattingclient.Model;

public class Response {
    public Response(String responseFunction, String responseParam) {
        this.responseFunction = responseFunction;
        this.responseParam = responseParam;
    }

    private String responseFunction;
    private String responseParam;

    public String getResponseFunction() {
        return responseFunction;
    }

    public void setResponseFunction(String responseFunction) {
        this.responseFunction = responseFunction;
    }

    public String getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(String responseParam) {
        this.responseParam = responseParam;
    }
}
