package cn.rongcloud.sealmicandroid.common.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.adapter.LiveDataCallAdapter;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * retrofit返回的结果由原生的call转为LiveData，对应的适配器工厂
 */
public class LiveDataCallFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("返回值需为参数化类型");
        }

        Class clazz = CallAdapter.Factory.getRawType(returnType);
        if (clazz != NetStateLiveData.class) {
            throw new IllegalArgumentException("返回值不是NetStateLiveData类型");
        }

        Type type = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);
        return new LiveDataCallAdapter<>(type);
    }
}