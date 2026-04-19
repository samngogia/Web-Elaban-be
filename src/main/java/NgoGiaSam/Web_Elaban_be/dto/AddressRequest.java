package NgoGiaSam.Web_Elaban_be.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private String fullName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String addressLine;
    private String addressType;     // "HOME" hoặc "OFFICE"
    private String note;
    private Boolean isDefault;
}
