package cn.rongcloud.sealmic.net.model;

/**
 * 网络请求结果基础类
 *
 * @param <R> 请求结果的实体类
 */
public class Result<R> {
    private Data<R> data;

    private int errCode;

    private String errDetail;

    private String errMsg;

    public Data<R> getData() {
        return data;
    }

    public void setData(Data<R> data) {
        this.data = data;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrDetail() {
        return errDetail;
    }

    public void setErrDetail(String errDetail) {
        this.errDetail = errDetail;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    /**
     * 获取请求结果中的的实体类
     *
     * @return R
     */
    public R getDataResult() {
        if (data != null) {
            return data.getResult();
        }

        return null;
    }

    public static class Data<R> {
        public R result;

        public R getResult() {
            return result;
        }

        public void setResult(R result) {
            this.result = result;
        }
    }
}
