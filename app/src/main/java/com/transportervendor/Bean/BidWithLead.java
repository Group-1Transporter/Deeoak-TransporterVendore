package com.transportervendor.Bean;

import java.io.Serializable;

public class BidWithLead implements Serializable {
    private Bid bid;

    private Leads leads;

    public BidWithLead() {
    }

    public BidWithLead(Bid bid, Leads leads) {
        this.bid = bid;
        this.leads = leads;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public Leads getLead() {
        return leads;
    }

    public void setLead(Leads leads) {
        this.leads = leads;
    }
}