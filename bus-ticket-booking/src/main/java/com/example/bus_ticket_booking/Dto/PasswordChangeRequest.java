package com.example.bus_ticket_booking.Dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordChangeRequest {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
