package io.swagger.client.model;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import com.hardskygames.luckycalories.launch.models.SignUp;
import com.mobandme.android.transformer.compiler.Mappable;
import com.mobandme.android.transformer.compiler.Mapped;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

@Mappable(with = SignUp.class)
public class SignUpInfo implements Serializable {

    @Mapped
    @SerializedName("name")
    private String name = null;

    @Mapped
    @SerializedName("email")
    private String email = null;

    @Mapped
    @SerializedName("password")
    private String password = null;


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
     **/
    @ApiModelProperty(value = "")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignUpInfo signUpInfo = (SignUpInfo) o;
        return Objects.equal(name, signUpInfo.name) &&
                Objects.equal(email, signUpInfo.email) &&
                Objects.equal(password, signUpInfo.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, email, password);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SignUpInfo {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
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
