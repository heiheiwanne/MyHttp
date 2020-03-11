package com.lucky.lib.lchttp2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class HibernateBaseDao<T> implements BaseDao<T> {
    private Class<T> entityClass;

    /**
     * 这个通常也是hibernate的取得子类class的方法
     *
     * @date 2010-4-11 下午01:51:28
     */
    public HibernateBaseDao() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    @Override
    public T get(String id) {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}