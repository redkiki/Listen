package com.example.listen;
import java.util.ArrayList;
import java.util.List;

/*****************************************
 * 自动生成的实体类
 * 使用高能gsonformat插件自动生成java实体类
 *****************************************/

class RespondBean
{
    private String from;
    private String to;
    public  List<TransResultBean> trans_result=new ArrayList<>();
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<TransResultBean> getTrans_result() {
        return trans_result;
    }
    public void innitial_list()
    {
        trans_result= new ArrayList<>();
    }
    public void setTrans_result(List<TransResultBean> trans_result)
    {
        this.trans_result = trans_result;
    }

    public static class TransResultBean
    {

        private String src;
        private String dst;
        public String getSrc() {
            return src;
        }
        public void setSrc(String src) {
            this.src = src;
        }
        public String getDst() {
            return dst;
        }
        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}

