package NgoGiaSam.Web_Elaban_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ReviewAdminResponse {
    private Long id;
    private String username;
    private String productName;
    private int rating;
    private String comment;
    private boolean approved;
    private boolean Hidden;
    private String adminReply;
    private LocalDateTime reviewDate;
    private LocalDateTime replyDate;

}
