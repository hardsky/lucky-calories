package io.swagger.client.api;

import io.swagger.client.CollectionFormats.*;


import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import io.swagger.client.model.User;
import io.swagger.client.model.Error;
import io.swagger.client.model.Calorie;
import io.swagger.client.model.LoginInfo;
import io.swagger.client.model.SignUpInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LuckyCaloriesApi {
  
  /**
   * 
   * Create new user.\n
   * @param user user info (required)
   * @return Call<User>
   */
  
  @POST("users")
  Call<User> createUser(
          @Body User user
  );

  
  /**
   * 
   * Add new item to calories list for user.\n
   * @param id user Id (required)
   * @param calorie Add calorie item to list (required)
   * @return Call<Calorie>
   */
  
  @POST("user/{id}/calories")
  Call<Calorie> createUserCalorie(
          @Path("id") Long id, @Body Calorie calorie
  );

  /**
   * 
   * Delete user.\n
   * @param id user Id (required)
   * @return Call<Void>
   */
  
  @DELETE("users/{id}")
  Call<Void> deleteUser(
          @Path("id") Long id
  );

  
  /**
   * 
   * Delete entry from calories list for user.\n
   * @param id user Id (required)
   * @param calorieId entry Id in calorie list (required)
   * @return Call<Void>
   */
  
  @DELETE("user/{id}/calories/{calorieId}")
  Call<Void> deleteUserCalorie(
          @Path("id") Long id, @Path("calorieId") Long calorieId
  );

  
  /**
   * 
   * Get user info.\n
   * @param id user Id (required)
   * @return Call<User>
   */
  
  @GET("users/{id}")
  Call<User> getUser(
          @Path("id") Long id
  );

  
  /**
   * 
   * Get paged calories list for user.\nPaged by 100 meals.\nOrdered by eat time (from now to past (closest to current time is first in list)).\n
   * @param id user Id (required)
   * @param last eat time of last meal from previous page (optional)
   * @return Call<List<Calorie>>
   */
  
  @GET("user/{id}/calories")
  Call<List<Calorie>> getUserCaloriesList(
          @Path("id") Long id, @Query("last") Long last
  );

  /**
   *
   * Get paged calories list for user.\nPaged by 100 meals.\nOrdered by eat time (from now to past (closest to current time is first in list)).\nWith applied filter by dates from-to, time from-to \n(e.g. how much calories have I had for lunch each day in the last month, if lunch is between 12 and 15h).\n
   * @param id user Id (required)
   * @param last eat time of last meal from previous page (optional)
   * @param fromDate  (optional)
   * @param toDate  (optional)
   * @param fromTime  (optional)
   * @param toTime  (optional)
   * @return Call<List<Calorie>>
   */

  @GET("user/{id}/calories/filter")
  Call<List<Calorie>> getUserCaloriesFilter(
          @Path("id") Long id, @Query("last") Long last, @Query("fromDate") Long fromDate, @Query("toDate") Long toDate, @Query("fromTime") Long fromTime, @Query("toTime") Long toTime
  );

  /**
   * 
   * Get user list.\nPaged by 100 users.\nOrdered by alphabeticaly.\n
   * @param last last name form user list (optional)
   * @return Call<List<User>>
   */
  
  @GET("users")
  Call<List<User>> getUserList(
          @Query("last") String last
  );

  
  /**
   * 
   * Authorize user.\n
   * @param info User login info in JSON format. (required)
   * @return Call<String>
   */
  
  @POST("login")
  Call<String> login(
          @Body LoginInfo info
  );

  
  /**
   * 
   * Register new user.\n
   * @param info User registration info in JSON format. (required)
   * @return Call<String>
   */
  
  @POST("signup")
  Call<String> signup(
          @Body SignUpInfo info
  );

  
  /**
   * 
   * Update user info.\n
   * @param user user info (required)
   * @return Call<User>
   */
  
  @PUT("users")
  Call<User> updateUser(
          @Body User user
  );

  
  /**
   * 
   * Update item in calories list for user.\n
   * @param id user Id (required)
   * @param calorie Changed calorie item (required)
   * @return Call<Calorie>
   */
  
  @PUT("user/{id}/calories")
  Call<Calorie> updateUserCalorie(
          @Path("id") Long id, @Body Calorie calorie
  );

  
}
