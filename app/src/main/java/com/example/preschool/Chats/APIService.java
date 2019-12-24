package com.example.preschool.Chats;


import com.example.preschool.Notifications.MyResponse;
import com.example.preschool.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAIx35tAY:APA91bEmiiEMH-kUFyQc85RWLxcp1ilyMDtDoKgthO6SIgGyrbpa1s2KqEXoSTNrKwT9mMbyCnFj32GPaEkeOO6-RtJmU5ADfNZVg3vPvifNrYOK3fY_RfpetjV5wGRZmOoI-LPDI02C"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
