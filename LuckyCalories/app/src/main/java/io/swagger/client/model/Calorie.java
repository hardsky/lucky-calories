package io.swagger.client.model;

import com.google.common.base.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class Calorie  implements Serializable {
  
  @SerializedName("id")
  private Long id = null;
  
  @SerializedName("meal")
  private String meal = null;
  
  @SerializedName("note")
  private String note = null;
  
  @SerializedName("amount")
  private Integer amount = null;
  
  @SerializedName("eatTime")
  private Long eatTime = null;
  

  
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
  public String getMeal() {
    return meal;
  }
  public void setMeal(String meal) {
    this.meal = meal;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  public String getNote() {
    return note;
  }
  public void setNote(String note) {
    this.note = note;
  }

  
  /**
   * Value in kcal
   **/
  @ApiModelProperty(value = "Value in kcal")
  public Integer getAmount() {
    return amount;
  }
  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  public Long getEatTime() {
    return eatTime;
  }
  public void setEatTime(Long eatTime) {
    this.eatTime = eatTime;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Calorie calorie = (Calorie) o;
    return Objects.equal(id, calorie.id) &&
        Objects.equal(meal, calorie.meal) &&
        Objects.equal(note, calorie.note) &&
        Objects.equal(amount, calorie.amount) &&
        Objects.equal(eatTime, calorie.eatTime);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, meal, note, amount, eatTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Calorie {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    meal: ").append(toIndentedString(meal)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    eatTime: ").append(toIndentedString(eatTime)).append("\n");
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
