package io.swagger.client.model;

import com.google.common.base.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class User  implements Serializable {
  
  @SerializedName("id")
  private Long id = null;
  
  @SerializedName("name")
  private String name = null;
  
  @SerializedName("email")
  private String email = null;
  
  @SerializedName("dailyCalories")
  private Integer dailyCalories = null;
  
  @SerializedName("userType")
  private Integer userType = null;
  

  
  /**
   **/
  @ApiModelProperty(value = "")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  
  /**
   * Daily calories limit
   **/
  @ApiModelProperty(value = "Daily calories limit")
  public Integer getDailyCalories() {
    return dailyCalories;
  }
  public void setDailyCalories(Integer dailyCalories) {
    this.dailyCalories = dailyCalories;
  }

  
  /**
   * user, manager, admin  \nUser - would only be able to CRUD on his owned records.  \nManager - same as User + would able to CRUD users  \nAdmin - same as Manager + would able to CRUD everything;  \nUser == 1;\nManager == 2;\nAdmin == 3;\n
   **/
  @ApiModelProperty(value = "user, manager, admin  \nUser - would only be able to CRUD on his owned records.  \nManager - same as User + would able to CRUD users  \nAdmin - same as Manager + would able to CRUD everything;  \nUser == 1;\nManager == 2;\nAdmin == 3;\n")
  public Integer getUserType() {
    return userType;
  }
  public void setUserType(Integer userType) {
    this.userType = userType;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equal(id, user.id) &&
        Objects.equal(name, user.name) &&
        Objects.equal(email, user.email) &&
        Objects.equal(dailyCalories, user.dailyCalories) &&
        Objects.equal(userType, user.userType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, email, dailyCalories, userType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    dailyCalories: ").append(toIndentedString(dailyCalories)).append("\n");
    sb.append("    userType: ").append(toIndentedString(userType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
