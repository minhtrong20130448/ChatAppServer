package dao.bean;

import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class UserBean {
    @ColumnName("id")
    private int id;
    @ColumnName("username")
    private String username = "";
    @ColumnName("password")
    private String password = "";
    @ColumnName("player_name")
    private String playerName = "";
    @ColumnName("gender")
    private int gender;
    @ColumnName("sponsor")
    private int sponsor;
    @ColumnName("email")
    private String email = "";
    @ColumnName("phone")
    private String phone = "";
    @ColumnName("active")
    private int active;
    @ColumnName("passwordRecoveryKey")
    private String passwordRecoveryKey = "";
    @ColumnName("passwordRecoveryTime")
    private long passwordRecoveryTime;
    @ColumnName("tree")
    private String tree = "";
    @ColumnName("relogin_token")
    private String reloginToken = "";
    @ColumnName("agency_level")
    private int agencyLevel;

    @ColumnName("phone_otp")
    private String phoneOtp = "";
    @ColumnName("phone_otp_time")
    private long phoneOtpTime;
    @ColumnName("is_phone_verified")
    private int isPhoneVerified;
    @ColumnName("email_code")
    private String emailCode = "";
    @ColumnName("email_code_time")
    private long emailCodeTime;
    @ColumnName("is_email_verified")
    private int isEmailVerified;
    @ColumnName("isBot")
    private int isBot;
}
