package io.swagger.client.model;

import com.google.common.base.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.User;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class AuthInfo  implements Serializable {

    @SerializedName("accessToken")
    private String accessToken = null;

    @SerializedName("user")
    private User user = null;

    /**
     **/
    @ApiModelProperty(value = "")
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthInfo authInfo = (AuthInfo) o;
        return Objects.equal(accessToken, authInfo.accessToken) &&
                Objects.equal(user, authInfo.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accessToken, user);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AuthInfo {\n");

        sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
        sb.append("    user: ").append(toIndentedString(user)).append("\n");
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
