package com.pro_pizza.domain;



import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="confirmationToken")
public class ConfirmationToken {

	   @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name="token_id")
	    private long tokenid;

	    @Column(name="confirmation_token")
	    private String confirmationToken;

	    @Temporal(TemporalType.TIMESTAMP)
	    private Date createdDate;

	    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	    @JoinColumn(nullable = false, name = "user_id")
	    private User user;


    public ConfirmationToken(User user) {
        this.user = user;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }



    // getters and setters
}