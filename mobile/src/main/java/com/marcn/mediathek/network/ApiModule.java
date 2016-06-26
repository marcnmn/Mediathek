package com.marcn.mediathek.network;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.marcn.mediathek.network.services.ArdService;
import com.marcn.mediathek.network.services.ZdfService;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class ApiModule {
    private static final String ZDF_BASE_URL = "http://heute-api.live.cellular.de/";
    private static final String ARD_BASE_URL = "http://www.ardmediathek.de/";

    @Provides
    @Singleton
    OkHttpClient provideOkHTTPClient() {
        return new OkHttpClient
                .Builder()
                .build();
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.addHandler(createDeserializationProblemHandler());
        return objectMapper;
    }


    private DeserializationProblemHandler createDeserializationProblemHandler() {
        return new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt,
                                                 JsonParser jp,
                                                 JsonDeserializer<?> deserializer,
                                                 Object pojo,
                                                 String propertyName) throws IOException {
//                Log.v("JSON", "unrecognized JSON property '" + propertyName + "' in pojo '"
//                        + pojo.getClass().getSimpleName() + "' (known properties are: "
//                        + deserializer.getKnownPropertyNames().toString() + ")");
                // its important to return false here, otherwise Jackson
                // won't evaluate the FAIL_ON_UNKNOWN_PROPERTIES flag!
                return false;
            }
        };
    }

    @Provides
    @Singleton
    ZdfService provideAssetService() {
        return provideZdfRetrofit(provideOkHTTPClient(), provideObjectMapper()).create(ZdfService.class);
    }

    @Provides
    @Singleton
    ArdService provideArdService() {
        return provideArdRetrofit(provideOkHTTPClient(), provideObjectMapper()).create(ArdService.class);
    }

    private Retrofit provideZdfRetrofit(final OkHttpClient okHttpClient,
                                        final ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl(ZDF_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(okHttpClient)
                .build();
    }


    private Retrofit provideArdRetrofit(final OkHttpClient okHttpClient,
                                        final ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl(ARD_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(okHttpClient)
                .build();
    }
}
