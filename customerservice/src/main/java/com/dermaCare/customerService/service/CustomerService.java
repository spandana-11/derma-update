package com.dermaCare.customerService.service;

import java.util.List;
import org.springframework.http.ResponseEntity;

import com.dermaCare.customerService.dto.BookingRequset;
import com.dermaCare.customerService.dto.ConsultationDTO;
import com.dermaCare.customerService.dto.CustomerDTO;
import com.dermaCare.customerService.dto.CustomerRatingDomain;
import com.dermaCare.customerService.dto.FavouriteDoctorsDTO;
import com.dermaCare.customerService.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface CustomerService {

//	public ResponseDTO signInOrSignUp(String fullName, String mobileNumber, HttpSession session);
//	
//	public ResponseDTO requestOtp(String mobileNumber, String fullName, HttpSession session);
//	
//	 public ResponseDTO verifyOtp(String enteredOtp, String mobileNumber, HttpSession session);
//	 
//	 public ResponseDTO resendOtp(String mobileNumber, HttpSession session);
	  public Response saveCustomerBasicDetails(CustomerDTO customerDTO);
	 
	  public Response getCustomerByMobileNumber(String mblnumber);
	 
	  public Response getAllCustomers() ;
	 
	  public Response updateCustomerBasicDetails( CustomerDTO customerDTO ,String mobileNumber) ;
	 
	  public Response deleteCustomerByMobileNumber(String mobileNumber);
	 
	  public CustomerDTO getCustomerDetailsByMobileNumber(String mobileNumber);
	
	  public CustomerDTO getCustomerDetailsByEmail(String email);
	
	  public List<CustomerDTO> getCustomerByfullName(String fullName);
    
      public Response saveConsultation(ConsultationDTO dto) ;
    
      public Response getAllConsultations();
	
    //BOOKING MANAGENET
    
    public Response bookService(BookingRequset req) throws JsonProcessingException ;
    
    public Response deleteBookedService(String id);
    
    public Response getBookedService(String id);
    
    public Response getCustomerBookedServices(
	    	String mobileNumber);
    
    public Response getAllBookedService();
    
    public Response getBookingByDoctorId(String doctorId);
    
    public Response getBookingByServiceId(String serviceId);
    
    public Response getBookingByClinicId(String clinicId);
    
    ///  DOCTOR APIS
    
    public Response getDoctors(String cid, String serviceId);
    
    public ResponseEntity<Response> saveFavouriteDoctors(FavouriteDoctorsDTO favouriteDoctorsDTO);
    
    public Response getDoctorsSlots(String hospitalId,String doctorId);
    
    public Response getAllSavedFavouriteDoctors();
   
    
   // RATING APPOINTMENT
    public Response submitCustomerRating(CustomerRatingDomain ratingRequest);
    
    public Response getRatingForService(String hospitalId,String doctorId);
    
    // SUBSERVICE
    public Response getSubServiceInfoBySubServiceId(String subServiceId) throws JsonProcessingException ;
    
    //DOCTORINFOBYSUBSERVICEID
    public Response getDoctorsandHospitalDetails(String hospitalId, String subServiceId)throws JsonProcessingException;
    
    //DetailsBySubServiceIdAndConsultationType
    public Response getDetailsBySubServiceIdAndConsultationType(String subServiceId);
}
