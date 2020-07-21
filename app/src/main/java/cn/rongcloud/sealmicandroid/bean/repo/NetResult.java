package cn.rongcloud.sealmicandroid.bean.repo;

/**
 * 网络请求返回的公共响应体
 */
public class NetResult<R> {


    /**
     * code : 10000
     * msg : OK
     * data : {"userId":"e85e72ca-c572-4c4d-b3af-6a78a644cdd0","userName":"秦昕迪","portrait":"","type":0,"authorization":"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyb25nY2xvdWQiLCJqdGkiOiJiX1FzcVJNLVM1SW1tSU9uZ2J3QTBJIiwiaWF0IjoxNTkxODc0MTM4LCJtYXJrIjoi44-Z5L2hwrTInuKLk-OghOibsuqsmeuCoeW7ge2bhuSsmeaeguGYh-S4q-aPpiIsImRhdGEiOiJ7XCJ1c2VySWRcIjpcImU4NWU3MmNhLWM1NzItNGM0ZC1iM2FmLTZhNzhhNjQ0Y2RkMFwiLFwidXNlck5hbWVcIjpcIuenpuaYlei_qlwiLFwicG9ydHJhaXRcIjpcIlwiLFwidHlwZVwiOjB9In0.wHyWgE0RrqvClVo4rqsXb-tcVvCI8A6XA-JDLVrhlrI","imToken":"fJIv1pGHpxwkAaj37vG7bSUGlKJUht+mIQo7Bcbc9ZGvdomngyXhus8zVcd/XKtWKBD+XBLGyCQvg8euEufevg==@nmsv.cn.rongnav.com;nmsv.cn.rongcfg.com"}
     */

    private int code;
    private String msg;
    private R data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }
}
