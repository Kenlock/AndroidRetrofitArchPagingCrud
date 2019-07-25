package com.melardev.android.crud.datasource.common.repositories;

import com.melardev.android.crud.datasource.remote.dtos.responses.SuccessResponse;

import java.util.List;

public interface BaseRepository<ID, T> {
    interface BaseListener {
        void onError(String[] messages);
    }

    interface ResultListListener<T> extends BaseListener {
        void onSuccess(List<T> todo);
    }

    interface ResultListener<T> extends BaseListener {
        void onSuccess(T todo);
    }

    interface SuccessListener extends BaseListener{
        void onSuccess(SuccessResponse response);
    }


    void getById(ID id, ResultListener<T> listener);

    void create(T todo, ResultListener<T> listener);

    void update(T entity, ResultListener<T> listener);

    void deleteById(ID id, SuccessListener listener);

    void delete(T entity, SuccessListener listener);
}
