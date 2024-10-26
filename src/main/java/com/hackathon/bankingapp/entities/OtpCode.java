package com.hackathon.bankingapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "otp_code")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OtpCode {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String code;

    @CreatedDate
    private LocalDateTime createdAt;

    private Boolean used;

}