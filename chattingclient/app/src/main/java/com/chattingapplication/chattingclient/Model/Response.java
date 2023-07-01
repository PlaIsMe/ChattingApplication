package com.chattingapplication.chattingclient.Model;

public class Response {
    public Response(String responseFunction, String responseParam, String responseClass, String responseContext) {
        this.responseFunction = responseFunction;
        this.responseParam = responseParam;
        this.responseClass = responseClass;
        this.responseContext = responseContext;
    }

    private String responseFunction;
    private String responseParam;
    private String responseClass;
    private String responseContext;


    public String getResponseContext() {
        return responseContext;
    }

    public void setResponseContext(String responseContext) {
        this.responseContext = responseContext;
    }

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
