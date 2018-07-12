package helloworld.demo.com.uploadfiles;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface GetUserClient {



    @Multipart

    @POST("apiretriveprofile.php")

    Call<Results> retrieveAccount(

            @Part("candidate_id") RequestBody id


    );
}
