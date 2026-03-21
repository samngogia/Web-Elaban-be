package NgoGiaSam.Web_Elaban_be.enity;

import lombok.Data;

@Data
public class ErrorLog {
    private  String Content;

    public ErrorLog(String content) {
        Content = content;
    }
}
