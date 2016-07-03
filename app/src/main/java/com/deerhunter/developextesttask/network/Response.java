package com.deerhunter.developextesttask.network;

public class Response {
    private int status = -1;
    private byte[] response;
    private String contentEncoding;

    public void setStatus(int status){
        this.status = status;
    }

    public void setResponse(byte[] response){
        this.response = response;
    }

    public int getStatus() {
        return status;
    }

    public byte[] getResponse(){
        return response;
    }

    @Override
    public String toString(){
        return String.format("Response{ status = %d, response = %s, }", status, new String(response));
    }

    public boolean isActual() {
        return status != -1;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }
}
