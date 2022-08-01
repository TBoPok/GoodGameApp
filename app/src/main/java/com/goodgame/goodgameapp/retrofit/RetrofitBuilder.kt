package com.goodgame.goodgameapp.retrofit

import com.goodgame.goodgameapp.models.*
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {
    @GET("clubs")
    suspend fun getClubs() : List<ClubModel>

    @FormUrlEncoded
    @POST("login")
    suspend fun sendLoginData(@Field("phone_number") phone_number : String,
                              @Field("club_id") club_id : String) : LoginResponse

    @FormUrlEncoded
    @POST("login/confirm")
    suspend fun sendConfirmData(@Field("confirm_key") confirm_key : String,
                                @Field("phone_number") phone_number : String,
                                @Field("club_id") club_id : String) : ConfirmCodeResponse

    @FormUrlEncoded
    @POST("login/token")
    suspend fun sendToken(@Field("private_key") private_key : String) : TokenConfirmResponse

    @FormUrlEncoded
    @POST("hero/info")
    suspend fun getHeroInfo(@Field("private_key") private_key : String) : HeroInfoResponse

    @FormUrlEncoded
    @POST("hero/create")
    suspend fun heroCreate(
        @Field("private_key") private_key : String,
        @Field("hero_type") hero_type : String) : HeroCreateResponse


    @FormUrlEncoded
    @POST("hero/skill")
    suspend fun setHeroSkill(
        @Field("private_key") private_key : String,
        @Field("skill_type") skill_type : String): SkillResponse

    @GET("images")
    fun getImage(@Query("image") name: String) : ByteArray
}

object RetrofitBuilder {

    private const val BASE_URL = "https://crmgoodgame.ru/telegram/api/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiInterface = getRetrofit().create(ApiInterface::class.java)
}

class ApiHelper(private val apiService: ApiInterface) {

    suspend fun getUsers() =
        apiService.getClubs()

    suspend fun sendLoginData(loginModel: LoginModel) =
        apiService.sendLoginData(phone_number = loginModel.phone_number, club_id = loginModel.club_id)

    suspend fun sendConfirmData(confirmCodeModel : ConfirmCodeModel) = apiService.sendConfirmData(
        confirm_key = confirmCodeModel.confirm_key,
        phone_number = confirmCodeModel.phone_number,
        club_id = confirmCodeModel.club_id
    )

    suspend fun sendToken(token : String) =
        apiService.sendToken(private_key = token)

    suspend fun getHeroInfo(token : String) =
        apiService.getHeroInfo(private_key = token)

    suspend fun heroCreate(token : String, hero_type: String) =
        apiService.heroCreate(private_key = token, hero_type = hero_type)

    suspend fun setHeroSkill(token: String, skill_type: String) =
        apiService.setHeroSkill(private_key = token, skill_type = skill_type)

    fun getImage(name: String) =
        apiService.getImage(name = name)
}