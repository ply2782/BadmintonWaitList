package com.jc.jcsports.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jc.jcsports.BuildConfig;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RFClient {
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static class LazyHolder {
        private static final RFClient INSTANCE = new RFClient();
    }

    private static RFServices serverInterface;

    private RFClient() {
        init();
    }

    public static RFClient getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static RFServices getServerInterface() {
        return serverInterface;
    }

    private synchronized void init() {
        if (serverInterface == null) {
            OkHttpClient defaultHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Content-Type", "text/plain;charset=utf-8")
                            .build();
                    return chain.proceed(request);

                }
            }).build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                    .serializeNulls()
                    .create();

            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.addInterceptor(logging);
            okHttpBuilder.connectTimeout(30, TimeUnit.SECONDS);
            okHttpBuilder.readTimeout(30, TimeUnit.SECONDS);
            okHttpBuilder.writeTimeout(30, TimeUnit.SECONDS);
            okHttpBuilder.retryOnConnectionFailure(true);

            serverInterface = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson)) // retrofit2 json 받기
                    .addConverterFactory(new NullOnEmptyConverterFactory()) // retrofit2 null 값 처리
                    .client(defaultHttpClient)
                    .build()
                    .create(RFServices.class);
        }

    }


    //TODO : retrofit2 null값 처리
    class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return (Converter<ResponseBody, Object>) body -> {
                if (body.contentLength() == 0) {
                    return null;
                }
                return delegate.convert(body);
            };
        }
    }

    class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "null"; //여기서 null일 경우에는 0으로 치환해서 보내게 됩니다. 핵심!
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

    class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

}

