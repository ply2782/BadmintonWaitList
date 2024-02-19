package com.jc.jcsports.utils;

import com.jc.jcsports.model.Calculate;
import com.jc.jcsports.model.Member;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RFServices {

    @POST("/{path}")
    Single<List<Member>> insertMember(@Path(value = "path") String path, @Body Map<String, String> params);

    @POST("/{path}")
    Single<List<String>> getAreaas(@Path(value = "path") String path);

    @POST("/{path}")
    Single<List<Member>> getMembers(@Path(value = "path") String path);

    @POST("/{path}")
    Single<Member> attendanceChk(@Path(value = "path") String path, @Body Map<String, Object> params);

    @FormUrlEncoded
    @POST("/{path}")
    Single<Void> getErrorLog(@Path(value = "path") String path, @Field("msg") String msg);

    @POST("/{path}")
    Single<Map<Integer, Calculate>> calculatedAttendanceList(@Path(value = "path") String path);


}
