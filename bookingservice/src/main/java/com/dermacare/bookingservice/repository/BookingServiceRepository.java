package com.dermacare.bookingservice.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.dermacare.bookingservice.entity.Booking;

@Repository
public interface BookingServiceRepository extends MongoRepository<Booking,String> {

	 public  List<Booking> findByMobileNumber(String mobileNumber);
	 public  List<Booking> findByDoctorId(String doctorId);
	 public  List<Booking> findBySubServiceId(String serviceId);
	 public  List<Booking> findByClinicId(String clinicId);
	 public Optional<Booking> findByBookingId(ObjectId id);
	
}
