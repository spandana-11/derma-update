package com.dermaCare.customerService.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "booking_Id")
@Data
public class CustomizedDatabaseSequence {
	@Id
	private String id;
	private long seq;

}
