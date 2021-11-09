package com.example.taskcontrol;

public class requests {
    public String id_to,id_from;
    public Integer status;
    public requests(){}
    public requests(String id_to, String id_from, Integer status){
        this.id_to = id_to;
        this.id_from = id_from;
        this.status = status;
    }
}
