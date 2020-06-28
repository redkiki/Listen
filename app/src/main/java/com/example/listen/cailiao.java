package com.example.listen;

public class cailiao
{
    private int cant_id;

    private String cant_cailiao;

    public cailiao(int cant_id,String can_cailiao)
    {
        this.cant_id = cant_id;
        this.cant_cailiao = can_cailiao;
    }

    public void setCant_cailiao(String cant_cailiao)
    {
        this.cant_cailiao = cant_cailiao;
    }

    public String getCant_cailiao()
    {
        return cant_cailiao;
    }

    public void setCant_id(int cant_id) {
        this.cant_id = cant_id;
    }

    public int getCant_id() {
        return cant_id;
    }
}
