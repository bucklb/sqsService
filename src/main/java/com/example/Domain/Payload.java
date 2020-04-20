package com.example.Domain;

public class Payload {

    String ref;
    String dtl;

    public Payload() {}

    public Payload(String ref, String dtl) {
        this.ref = ref;
        this.dtl = dtl;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDtl() {
        return dtl;
    }

    public void setDtl(String dtl) {
        this.dtl = dtl;
    }
}
