package com.deerhunter.developextesttask.model;

import java.util.ArrayList;
import java.util.List;

public class Page {
    public String url;
    public int contentSize;
    public List<String> links = new ArrayList<>();
    public Status status = new Status(Status.State.READY_TO_LOAD, "");

    public Page(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Page{" +
                "url='" + url + '\'' +
                ", status=" + status + '\'' +
                ", links=" + convertListToString(links) +
                ", contentSize='" + contentSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return url.equals(page.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    private String convertListToString(List<String> links) {
        StringBuilder builder = new StringBuilder();
        for (String link: links) {
            builder.append(link).append(", ");
        }
        return builder.toString();
    }

    public static class Status {
        public State state;
        public String additionalInfo;
        public Status(State state, String additionalInfo) {
            this.state = state;
            this.additionalInfo = additionalInfo;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "state=" + state +
                    ", additionalInfo='" + additionalInfo + '\'' +
                    '}';
        }

        public enum State {
            READY_TO_LOAD,
            LOADING,
            PARSING,
            FOUND,
            NOT_FOUND,
            ERROR
        }
    }
}
