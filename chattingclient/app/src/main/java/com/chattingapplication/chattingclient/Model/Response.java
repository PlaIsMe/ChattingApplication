package com.chattingapplication.chattingclient.Model;

public class Response {
    public Response(String responseFunction, String responseParam, String responseClass) {
        this.responseFunction = responseFunction;
        this.responseParam = responseParam;
        this.responseClass = responseClass;
    }

    private String responseFunction;
    private String responseParam;
    private String responseClass;

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

    public String getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(String responseClass) {
        this.responseClass = responseClass;
    }

}
