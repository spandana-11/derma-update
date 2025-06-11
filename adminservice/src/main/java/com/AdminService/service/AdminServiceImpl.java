 package com.AdminService.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.AdminService.dto.AdminHelper;
import com.AdminService.dto.CategoryDto;
import com.AdminService.dto.ClinicCredentialsDTO;
import com.AdminService.dto.ClinicDTO;
import com.AdminService.dto.CustomerDTO;
import com.AdminService.dto.ServicesDto;
import com.AdminService.dto.SubServicesInfoDto;
import com.AdminService.dto.UpdateClinicCredentials;
import com.AdminService.entity.Admin;
import com.AdminService.entity.Clinic;
import com.AdminService.entity.ClinicCredentials;
import com.AdminService.feign.CssFeign;
import com.AdminService.feign.CustomerFeign;
import com.AdminService.repository.AdminRepository;
import com.AdminService.repository.ClinicCredentialsRepository;
import com.AdminService.repository.ClinicRep;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;

import feign.FeignException;


@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private ClinicRep clinicRep;
	
	@Autowired
	private ClinicCredentialsRepository clinicCredentialsRepository;
	
	@Autowired
	private CssFeign cssFeign;
	
	@Autowired
	private CustomerFeign customerFeign;
	
	@Override
	public Response adminRegister(AdminHelper helperAdmin) {
		Response response = new Response();
	try {
		Optional<Admin> userName = adminRepository.findByUserName(helperAdmin.getUserName());
		   Admin mobileNumber = adminRepository.findByMobileNumber(helperAdmin.getMobileNumber());
		   if(mobileNumber != null ) {
			   response.setMessage("MobileNumber is Already Exist");
		        response.setStatus(409);
		        response.setSuccess(false);
		        return response;
		   }
		        if(userName.isPresent()) {
		        	response.setMessage("UserName already exist");
			        response.setStatus(409);
			        response.setSuccess(false);
			        return response;
		        	}else {
		        	Admin entityAdmin = new Admin();
		 		    entityAdmin.setUserName(helperAdmin.getUserName());
		 		    entityAdmin.setPassword(helperAdmin.getPassword());
		 		    entityAdmin.setMobileNumber(helperAdmin.getMobileNumber());
		        adminRepository.save(entityAdmin);
		        response.setMessage("Credentials Are saved successfully");
		        response.setStatus(200);
		        response.setSuccess(true);
		        return response;
		}}catch(Exception e) {
		response.setMessage(e.getMessage());
        response.setStatus(500);
        response.setSuccess(false);
        return response;
	}
	}
	
	@Override
	public Response adminLogin(String userName, String password) {
		Response response = new Response();
		try {
			Optional<Admin> ExistUserName = adminRepository.findByUserName(userName);
			if(!ExistUserName.isPresent()) {
				response.setMessage("Incorrect UserName");
		        response.setStatus(401);
		        response.setSuccess(false);
		        return response;
			}
			
			Optional<Admin> credentials = adminRepository.findByUsernameAndPassword(userName, password);
			if(credentials.isPresent()) {
				response.setMessage("Login Successful");
		        response.setStatus(200);
		        response.setSuccess(true);
		        return response;
			}else {
				response.setMessage("Incorrect Password");
		        response.setStatus(401);
		        response.setSuccess(false);
		        return response;
			}
		}catch(Exception e) {
			response.setMessage(e.getMessage());
	        response.setStatus(500);
	        response.setSuccess(false);
	        return response;
		}
		
	}
	
	//CLINIC MANAGEMENT
			
	// Create Clinic
	@Override
		 public Response createClinic(ClinicDTO clinic) {
		        Response response = new Response();
		        try {
		        	Clinic clnc = clinicRep.findByContactNumber(clinic.getContactNumber());
		        	if(clnc != null) {
		        		response.setMessage("ContactNumber Is Already Exist");
			            response.setSuccess(false);
			            response.setStatus(409);
			            return response;
		        	}
		        	Clinic savedClinic = new Clinic();
		        	savedClinic.setAddress(clinic.getAddress());
		        	savedClinic.setCity(clinic.getCity());
		        	List<byte[]> docs = new ArrayList<>();
		        	try {
		        	for(String document : clinic.getHospitalDocuments()) {
		        		byte[] doc = Base64.getDecoder().decode(document);
		        	docs.add(doc);
		        	}}catch(Exception e) {
		        	 throw new IllegalArgumentException("Invalid Base64 in hospitalDocuments");
		        	}
		        	savedClinic.setHospitalDocuments(docs);	
		        	List<byte[]> contractors = new ArrayList<>();
		        	try {
		        	for(String document : clinic.getContractorDocuments()) {
		        		byte[] contractor = Base64.getDecoder().decode(document);
		        		contractors.add(contractor);
		        	}}catch(Exception e) {
		        	 throw new IllegalArgumentException("Invalid Base64 in contractorDocuments");
		        	}
		        	savedClinic.setContractorDocuments(contractors);;	
		        	savedClinic.setHospitalId(generateHospitalId());
		        	try {
		        	savedClinic.setHospitalLogo(Base64.getDecoder().decode(clinic.getHospitalLogo()));
		        	}catch(Exception e) {
		        		throw new IllegalArgumentException("Invalid Base64 in hospitalLogo");
		        	}
		        	savedClinic.setClosingTime(clinic.getClosingTime());
		        	savedClinic.setContactNumber(clinic.getContactNumber());
		        	savedClinic.setName(clinic.getName());
		        	savedClinic.setOpeningTime(clinic.getOpeningTime());
		        	savedClinic.setHospitalRegistrations(clinic.getHospitalRegistrations());
		        	savedClinic.setEmailAddress(clinic.getEmailAddress());
		        	savedClinic.setWebsite(clinic.getWebsite());
		        	savedClinic.setLicenseNumber(clinic.getLicenseNumber());
		        	savedClinic.setIssuingAuthority(clinic.getIssuingAuthority());	
		        	savedClinic.setRecommended(clinic.isRecommended());
		                    Clinic c = clinicRep.save(savedClinic);
		                    String username=c.getHospitalId();
		                    String rawpassword=generatePassword(9);
		        	if(c != null) {
		            ClinicCredentials clinicCredentials = new ClinicCredentials();
		            clinicCredentials.setUserName(username);
		            clinicCredentials.setPassword(rawpassword);
		            clinicCredentials.setHospitalName(c.getName());
		            clinicCredentialsRepository.save(clinicCredentials);
		            Map<String, Object> data=new HashMap<>();
		           // data.put("clinic", c);
		            data.put("clinicUsername", username);
		            data.put("clinicTemporaryPassword",rawpassword );
		            response.setData(data);
		            response.setMessage("Clinic created successfully");
		            response.setSuccess(true);
		            response.setStatus(200);
		            return response;
		            }
		        	
		        } catch (Exception e) {
		            response.setMessage("Error occurred while creating the clinic: " + e.getMessage());
		            response.setSuccess(false);
		            response.setStatus(500); // HTTP Status for Internal Server Error
		        }
		        return response;
		    }

    
	@Override
    public Response getClinicById(String clinicId) {
    	 Response response = new Response();
    	try {
        Clinic clinic = clinicRep.findByHospitalId(clinicId);
        if (clinic !=  null) {
        ClinicDTO clnc = new  ClinicDTO();
        clnc.setAddress(clinic.getAddress());
        clnc.setCity(clinic.getCity());
        List<String> docs = new ArrayList<>();
        for(byte[] document : clinic.getHospitalDocuments()) {
    		String doc = Base64.getEncoder().encodeToString(document);
    	docs.add(doc);	
    	}
        clnc.setHospitalDocuments(docs);
        clnc.setHospitalId(clinic.getHospitalId());
        clnc.setHospitalLogo(Base64.getEncoder().encodeToString(clinic.getHospitalLogo()));
        clnc.setHospitalRegistrations(clinic.getHospitalRegistrations());
        clnc.setEmailAddress(clinic.getEmailAddress());
        clnc.setWebsite(clinic.getWebsite());
        clnc.setLicenseNumber(clinic.getLicenseNumber());
        clnc.setIssuingAuthority(clinic.getIssuingAuthority());
        clnc.setClosingTime(clinic.getClosingTime());
        clnc.setContactNumber(clinic.getContactNumber());
        clnc.setName(clinic.getName());
        clnc.setOpeningTime(clinic.getOpeningTime());
        clnc.setEmailAddress(clinic.getEmailAddress());
        clnc.setEmailAddress(clinic.getEmailAddress());
        clnc.setLicenseNumber(clinic.getLicenseNumber());
        clnc.setIssuingAuthority(clinic.getIssuingAuthority()); 
        clnc.setRecommended(clinic.isRecommended());
        List<String> files = new ArrayList<>();
        for(byte[] document : clinic.getContractorDocuments()) {
    		String doc = Base64.getEncoder().encodeToString(document);
    	files.add(doc);	
    	}
        clnc.setContractorDocuments(files);
            response.setMessage("Clinic fetched successfully");
            response.setSuccess(true);
            response.setStatus(200); // HTTP Status for OK
            response.setData(clnc);
            return response;
        } else {
            response.setMessage("Clinic not found");
            response.setSuccess(false);
            response.setStatus(404);
            return response;// HTTP Status for Not Found
        }}catch(Exception e) {
        	 response.setMessage(e.getMessage());
             response.setSuccess(false);
             response.setStatus(500);
             return response;
        }
    }
    

	@Override
    public Response getAllClinics() {
    	 Response  response = new  Response();
    	try {
    		 List<Clinic> clinics = clinicRep.findAll();
    		 List<ClinicDTO> list  = new ArrayList<>();
    		  List<String> docs = new ArrayList<>();
    		 if(!clinics.isEmpty()) {
    			 for(Clinic clinic :clinics) {
    				 ClinicDTO clnc =  new ClinicDTO();
    				  clnc.setAddress(clinic.getAddress());
    			        clnc.setCity(clinic.getCity());
    			        for(byte[] document : clinic.getHospitalDocuments()) {
    			    		String doc = Base64.getEncoder().encodeToString(document);
    			    	docs.add(doc);	
    			    	}
    			        List<String> files = new ArrayList<>();
    			        for(byte[] document : clinic.getContractorDocuments()) {
    			    		String doc = Base64.getEncoder().encodeToString(document);
    			    	files.add(doc);	
    			    	}
    			        clnc.setContractorDocuments(files);
    			        clnc.setHospitalDocuments(docs);
    			        clnc.setHospitalId(clinic.getHospitalId());
    			        clnc.setHospitalLogo(Base64.getEncoder().encodeToString(clinic.getHospitalLogo()));
    			        clnc.setHospitalRegistrations(clinic.getHospitalRegistrations());
    			        clnc.setEmailAddress(clinic.getEmailAddress());
    			        clnc.setWebsite(clinic.getWebsite());
    			        clnc.setLicenseNumber(clinic.getLicenseNumber());
    			        clnc.setIssuingAuthority(clinic.getIssuingAuthority());
    			        clnc.setClosingTime(clinic.getClosingTime());
    			        clnc.setContactNumber(clinic.getContactNumber());
    			        clnc.setName(clinic.getName());
    			        clnc.setOpeningTime(clinic.getOpeningTime());
    			        clnc.setEmailAddress(clinic.getEmailAddress());
    			        clnc.setEmailAddress(clinic.getEmailAddress());
    			        clnc.setLicenseNumber(clinic.getLicenseNumber());
    		        	clnc.setHospitalId(clinic.getHospitalId());
    		        	clnc.setRecommended(clinic.isRecommended());
    		        	list.add(clnc);
   			 } response.setData(list);
    			 response.setMessage("fetched successfully");
    			 response.setStatus(200);
    			 response.setSuccess(true);
    			 return response;
    			  }else {
    	    	         response.setData(null);
    	    			 response.setMessage("Clinics Not Found ");
    	    			 response.setStatus(404);
    	    			 response.setSuccess(false);
    	    			 return response;
    		 }}catch(Exception e) {
    			 response.setData(null);
    			 response.setMessage(e.getMessage());
    			 response.setStatus(500);
    			 response.setSuccess(false);
    			 return response;
    		 }
    		
    	}
    
	@Override
    public Response updateClinic(String clinicId, ClinicDTO clinic) {
        Response response = new Response();
        try {
            Clinic savedClinic = clinicRep.findByHospitalId(clinicId);
            if (savedClinic != null) {
            	if (clinic.getAddress() != null) savedClinic.setAddress(clinic.getAddress());
            	if (clinic.getCity() != null) savedClinic.setCity(clinic.getCity());
            	if (clinic.getHospitalDocuments() != null) {
            	    List<byte[]> docs = new ArrayList<>();
            	    for (String document : clinic.getHospitalDocuments()) {
            	        if (document != null) {
            	            docs.add(Base64.getDecoder().decode(document));
            	        }
            	    }
            	    savedClinic.setHospitalDocuments(docs);}
            	if (clinic.getHospitalLogo() != null) {
            	    savedClinic.setHospitalLogo(Base64.getDecoder().decode(clinic.getHospitalLogo()));}
            	if (clinic.getClosingTime() != null) savedClinic.setClosingTime(clinic.getClosingTime());
            	if (clinic.getContactNumber() != null) savedClinic.setContactNumber(clinic.getContactNumber());
            	if (clinic.getName() != null) {
            		savedClinic.setName(clinic.getName());
            		ClinicCredentials cdls = clinicCredentialsRepository.findByUserName(savedClinic.getHospitalId());
            		cdls.setUserName(cdls.getUserName());
            		cdls.setPassword(cdls.getPassword());
            		cdls.setHospitalName(clinic.getName());
            		clinicCredentialsRepository.save(cdls);}
            	if (clinic.getOpeningTime() != null) savedClinic.setOpeningTime(clinic.getOpeningTime());
            	if (clinic.getHospitalRegistrations() != null) savedClinic.setHospitalRegistrations(clinic.getHospitalRegistrations());
            	if(clinic.getContractorDocuments() != null ) {
            		List<byte[]> files = new ArrayList<>();
    	        	try {
    	        	for(String document : clinic.getContractorDocuments()) {
    	        		byte[] doc = Base64.getDecoder().decode(document);
    	        	files.add(doc);
    	        	}}catch(Exception e) {
    	        	 throw new IllegalArgumentException("Invalid Base64 in hospitalDocuments");
    	        	}
    	        	savedClinic.setContractorDocuments(files);
            	}
            	if (clinic.getEmailAddress() != null) savedClinic.setEmailAddress(clinic.getEmailAddress());
            	 savedClinic.setRecommended(clinic.isRecommended());
            	if (clinic.getWebsite() != null) savedClinic.setWebsite(clinic.getWebsite());
            	if (clinic.getLicenseNumber() != null) savedClinic.setLicenseNumber(clinic.getLicenseNumber());
            	if (clinic.getIssuingAuthority() != null) savedClinic.setIssuingAuthority(clinic.getIssuingAuthority());
                if (clinic.getHospitalId() != null) {
            	    savedClinic.setHospitalId(clinic.getHospitalId());
            	}
                Clinic updated = clinicRep.save(savedClinic);
                    response.setMessage("Clinic updated successfully");
                    response.setSuccess(true);
                    response.setStatus(200);
                    return response;
               } else {
                response.setMessage("Clinic not found for update");
                response.setSuccess(false);
                response.setStatus(404);
                return response;
            }
        } catch (Exception e) {
            response.setMessage("Error occurred while updating the clinic: " + e.getMessage());
            response.setSuccess(false);
            response.setStatus(500);
            return response;
        }
   
    }

	@Override
    public Response deleteClinic(String clinicId) {
        Response responseDTO = new Response();
        try {
            Clinic clinic = clinicRep.findByHospitalId(clinicId);
            if (clinic != null) {
                clinicRep.deleteByHospitalId(clinicId);
                clinicCredentialsRepository.deleteByUserName(clinicId);
                responseDTO.setMessage("Clinic deleted successfully");
                responseDTO.setSuccess(true);
                responseDTO.setStatus(200); // HTTP Status for OK
                return responseDTO ;
            } else {
                responseDTO.setMessage("Clinic not found for deletion");
                responseDTO.setSuccess(false);
                responseDTO.setStatus(404); // HTTP Status for Not Found
                return responseDTO ;
            }
        } catch (Exception e) {
            responseDTO.setMessage("Error occurred while deleting the clinic: " + e.getMessage());
            responseDTO.setSuccess(false);
            responseDTO.setStatus(500); // HTTP Status for Internal Server Error
        }
        return responseDTO;
    }
    
    
    //GENERATE RANDOM PASSWORD
    
    private static String generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4.");
        }
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$&_";
        Random random = new Random();
        // First character - must be uppercase
        char firstChar = upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length()));

        // Ensure at least one special character and one digit
        char specialChar = specialChars.charAt(random.nextInt(specialChars.length()));
        char digit = digits.charAt(random.nextInt(digits.length()));

        // Remaining characters pool
        String allChars = upperCaseLetters + lowerCaseLetters + digits + specialChars;
        StringBuilder remaining = new StringBuilder();

        for (int i = 0; i < length - 3; i++) {
            remaining.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Build the password and shuffle to randomize the positions (except first char)
        List<Character> passwordChars = new ArrayList<>();
        for (char c : remaining.toString().toCharArray()) {
            passwordChars.add(c);
        }

        // Add guaranteed special and digit
        passwordChars.add(specialChar);
        passwordChars.add(digit);

        // Shuffle rest except first character
        Collections.shuffle(passwordChars);
        StringBuilder password = new StringBuilder();
        password.append(firstChar);
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }
    
    // METHOD TO GENERATE SEQUANTIAL HOSPITAL ID
    public String generateHospitalId() {
        // Fetch the last clinic document ordered by HospitalId in descending order
        Clinic lastClinic = clinicRep.findFirstByOrderByHospitalIdDesc();
        
        // If no clinics exist in the database, return the first hospital ID "H_1"
        if (lastClinic == null) {
            return "H_1";
        }

        // Get the last HospitalId from the last clinic record
        String lastHospitalId = lastClinic.getHospitalId();
        
        // Define the regex pattern to match "H_" followed by a number (e.g., H_123)
        Pattern pattern = Pattern.compile("H_(\\d+)");
        Matcher matcher = pattern.matcher(lastHospitalId);

        // If the last ID is in the expected format (H_1, H_2, etc.)
        if (matcher.matches()) {
            // Extract the numeric part from the last hospital ID
            String numberPart = matcher.group(1);
            
            // Increment the numeric part by 1 to generate the next ID
            int nextNumber = Integer.parseInt(numberPart) + 1;
            
            // Return the new HospitalId in the format "H_{nextNumber}"
            return "H_" + nextNumber;
        } else {
            // If no valid format is found, return "H_1" (indicating the first hospital)
            return "H_1";
        }
    }
	
    
// CLINIC CREDENTIALS CRUD
    
    @Override
    public Response getClinicCredentials(String userName) {
        Response response = new Response();
        try {
            ClinicCredentials clinicCredentials = clinicCredentialsRepository.findByUserName(userName);
            if (clinicCredentials != null) {
            	ClinicCredentialsDTO clinicCredentialsDTO = new ClinicCredentialsDTO();
            	clinicCredentialsDTO.setUserName(clinicCredentials.getUserName());
            	clinicCredentialsDTO.setPassword(clinicCredentials.getPassword());
            	clinicCredentialsDTO.setHospitalName(clinicCredentials.getHospitalName());
                response.setSuccess(true);
                response.setData(clinicCredentialsDTO );
                response.setMessage("Clinic Credentials Found.");
                response.setStatus(200); // HTTP status for OK
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Clinic Credentials Are Not Found.");
                response.setStatus(404); // HTTP status for Not Found
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error Retrieving Clinic Credentials: " + e.getMessage());
            response.setStatus(500); // Internal server error
        }
        return response;
    }

    @Override
    public Response updateClinicCredentials(UpdateClinicCredentials credentials,String userName) {
        Response response = new Response();
        try {	
           ClinicCredentials existingCredentials = clinicCredentialsRepository.
           findByUserNameAndPassword(userName,credentials.getPassword());
           ClinicCredentials existUserName = clinicCredentialsRepository.findByUserName(userName);
           if(existUserName == null) {
        	   response.setSuccess(false);
               response.setMessage("Incorrect UserName");
               response.setStatus(401);
               return response;
           }
            if (existingCredentials != null) {
            if( credentials.getNewPassword().equalsIgnoreCase(credentials.getConfirmPassword())) {
               existingCredentials.setPassword(credentials.getNewPassword());
            	ClinicCredentials c = clinicCredentialsRepository.save(existingCredentials);
            	if(c != null) {
            	response.setSuccess(true);
                response.setData(null);
                response.setMessage("Clinic Credentials Updated Successfully.");
                response.setStatus(200);
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Failed To Upddate Clinic Credentials.");
                response.setStatus(404); 
                return response;// HTTP status for Not Found
            }}else {
            	 response.setSuccess(false);
                 response.setMessage("New password and confirm password do not match.");
                 response.setStatus(401);
            	return response;
            }}else {
            	response.setSuccess(false);
                response.setMessage("Incorrect Password.");
                response.setStatus(401);
           	return response;
            }}
           catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error updating clinic credentials: " + e.getMessage());
            response.setStatus(500); // Internal server error
        return response;}
    }

    @Override
    public Response deleteClinicCredentials(String userName ) {
        Response response = new Response();
        try {
            ClinicCredentials clinicCredentials = clinicCredentialsRepository.findByUserName(userName);
            if (clinicCredentials != null) {
                clinicCredentialsRepository.delete(clinicCredentials);
                clinicRep.deleteByHospitalId(userName);
                response.setSuccess(true);
                response.setMessage("Clinic Credentials Deleted Successfully.");
                response.setStatus(200); // HTTP status for OK
                return response;
            } else {
                response.setSuccess(false);
                response.setMessage("Clinic Credentials Are Not Found.");
                response.setStatus(404); // HTTP status for Not Found
                return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error Deleting Clinic Credentials: " + e.getMessage());
            response.setStatus(500); // Internal server error
        }
        return response;
    }
    
    @Override
    public Response login(ClinicCredentialsDTO credentials) {
    	Response response = new Response();
    	try {
    	String userName = credentials.getUserName();
    	String password = credentials.getPassword();
    	ClinicCredentials existUserName = clinicCredentialsRepository.findByUserName(userName);
    	if(userName == null || userName.isBlank()) {
    		response.setSuccess(false);
    		response.setMessage("Username is Required");
    		response.setStatus(400);	
    		return response;
    		}
    		if(existUserName == null) {
    			response.setSuccess(false);
        		response.setMessage("Incorrect UserName");
        		response.setStatus(401);	
        		return response;
        		}
    	if(password == null || password.isBlank()) {
    		response.setSuccess(false);
    		response.setMessage("Password is Required");
    		response.setStatus(400);	
    		return response;
    		}
    ClinicCredentials clinicCredentials =  clinicCredentialsRepository.
    	findByUserNameAndPassword(userName, password);
    	if(clinicCredentials != null) {
    		response.setSuccess(true);
    		response.setMessage("Login Successful");
    		response.setStatus(200);
    		response.setHospitalName(clinicCredentials.getHospitalName());
    		response.setHospitalId(clinicCredentials.getUserName());
    		return response;
    		}
    	else {
    		response.setSuccess(false);
    		response.setMessage("Incorrect Password");
    		response.setStatus(401);	//unauthorized
    		return response;
    	}}
    	catch(Exception e){
    		response.setSuccess(false);
    		response.setMessage(e.getMessage());
    		response.setStatus(500);
    		return response;
    	}
    }

	
   // Category Management
    
    @Override
    public Response addNewCategory(CategoryDto dto){
    	 Response response = new  Response();
    	 try {
	    		ResponseEntity<ResponseStructure<CategoryDto>> res = cssFeign.addNewCategory(dto);
	    		  if(res.hasBody()) {
		    		    ResponseStructure<CategoryDto> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }}catch(FeignException e) {
	                    	            response.setStatus(e.status());
	                	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	                	    			response.setSuccess(false);
	                    	        }
    	                                return response;
	                    	    } 
    
    @Override                                
	   public Response getAllCategory() {
	             Response response = new  Response();
	    	     try {
	    		 ResponseEntity<ResponseStructure<List<CategoryDto>>> res =  cssFeign.getAllCategory();
	    		  if(res.hasBody()) {
	    			  ResponseStructure<List<CategoryDto>> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
        	            response.setStatus(e.status());
    	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
    	    			response.setSuccess(false);
        	        }
                        return response;
        	    } 
	
    @Override                
	public Response getCategoryById(String CategoryId){
		 Response response = new  Response();
		try {
			ResponseEntity<ResponseStructure<CategoryDto>> res =  cssFeign.getCategoryById(CategoryId);
			 if(res.hasBody()) {
	    		    ResponseStructure<CategoryDto> rs = res.getBody();
	    			response.setData(rs);
	    			response.setStatus(rs.getHttpStatus().value());
                 }
	    		}catch(FeignException e) {
    	            response.setStatus(e.status());
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
    	        }
                    return response;
    	    } 
    
    @Override
	public Response deleteCategoryById(
			 String categoryId) {
		 Response response = new  Response();
	    	try {
	    		  ResponseEntity<ResponseStructure<CategoryDto>> res =  cssFeign.deleteCategoryById(categoryId);
	    			if(res.hasBody()) {
	    		    ResponseStructure<CategoryDto> rs = res.getBody();
	    			response.setData(rs);
	    			response.setStatus(rs.getHttpStatus().value());
                    }
	    		}catch(FeignException e) {
    	            response.setStatus(e.status());
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
    	        }
                    return response;
    	    } 
    
    @Override
	public Response updateCategory(String categoryId,CategoryDto updatedCategory){
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<ResponseStructure<CategoryDto>> res =  cssFeign.updateCategory(new ObjectId(categoryId), updatedCategory);
	    		  if(res.hasBody()) {
		    		    ResponseStructure<CategoryDto> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	 
	
	
	// SERVICES MANAGEMENT
	
    @Override
	public Response addService( ServicesDto dto){
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<ResponseStructure<ServicesDto>>  res =  cssFeign.addService(dto);
	    		  if(res.hasBody()) {
	    			  ResponseStructure<ServicesDto> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	 
	
    @Override
	public Response getServiceById( String categoryId){
		 Response response = new  Response();
	    	try {
	    		 ResponseEntity<ResponseStructure<List<ServicesDto>>>  res =  cssFeign.getServiceById(categoryId);
	    		  if(res.hasBody()) {
	    			  ResponseStructure<List<ServicesDto>> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	 
	
    @Override
	public Response getServiceByServiceId( String serviceId){
		 Response response = new  Response();
	    	try {
	    	ResponseEntity<ResponseStructure<ServicesDto>>  res =  cssFeign.getServiceByServiceId(serviceId);
	    		  if(res.hasBody()) {
	    			  ResponseStructure<ServicesDto> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	
    @Override
	public Response deleteService( String serviceId) {
		 Response response = new  Response();
	    	try {
	    	ResponseEntity<ResponseStructure<String>>  res =  cssFeign.deleteService(serviceId);
	    		  if(res.hasBody()) {
	    			  ResponseStructure<String> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	
	
    @Override
	public Response updateByServiceId( String serviceId,
			@RequestBody ServicesDto domainServices) {
		 Response response = new  Response();
	    	try {
	    	ResponseEntity<ResponseStructure<ServicesDto>>  res =  cssFeign.
	    			updateByServiceId(serviceId, domainServices);
	    		  if(res.hasBody()) {
	    			  ResponseStructure<ServicesDto> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	
    @Override
	public Response getAllServices() {
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<ResponseStructure<List<ServicesDto>>> res =  cssFeign.getAllServices();
	    	
	    		  if(res.hasBody()) {
	    			  ResponseStructure<List<ServicesDto>> rs = res.getBody();
		    			response.setData(rs);
		    			response.setStatus(rs.getHttpStatus().value());
	                    }
		    		}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;
	    	    } 	
	
	
	//SUBSERVICE MANAGEMENT
	
    @Override
	public  Response addSubService( SubServicesInfoDto dto){
		Response response = new Response();
	    	try {
	    		ResponseEntity<Response> res = cssFeign.addSubService(dto);
	    		return res.getBody();
	    	 
		    		}catch(FeignException e) {
	    	            response.setStatus(500);
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
		    			return response;
	    	        }
	                    
	    	    } 	 
	
    @Override
	public Response getSubServiceByIdCategory(String categoryId){
		Response response = new Response();
    	try {
    		ResponseEntity<Response> res = cssFeign.getSubServiceByIdCategory(categoryId);
    		return res.getBody();
	    		}catch(FeignException e) {
    	            response.setStatus(500);
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
	    			return response;
    	        }
                    
	    	    } 	 
	
    @Override
	public Response getSubServicesByServiceId(String serviceId){
		Response response = new Response();
    	try {
    		ResponseEntity<Response> res = cssFeign.getSubServicesByServiceId(serviceId);
    		return res.getBody();
    	 
	    		}catch(FeignException e) {
    	            response.setStatus(500);
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
	    			return response;
    	        }
                    
	    	    } 	
		
    @Override
	public Response getSubServiceBySubServiceId(String subServiceId){
		Response response = new Response();
    	try {
    		ResponseEntity<Response> res = cssFeign.getSubServiceBySubServiceId(subServiceId);
    		return res.getBody();
    	 
	    		}catch(FeignException e) {
    	            response.setStatus(500);
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
	    			return response;
    	        }
                    
	    	    } 	
	
    @Override
	public Response deleteSubService(String subServiceId){
		Response response = new Response();
    	try {
    		ResponseEntity<Response> res = cssFeign.deleteSubService(subServiceId);
    		return res.getBody();
    	 
	    		}catch(FeignException e) {
    	            response.setStatus(500);
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
	    			return response;
    	        }
                    
	}
    @Override
	public Response updateBySubServiceId(String subServiceId, SubServicesInfoDto domainServices) {
		Response response = new Response();
    	try {
    		ResponseEntity<Response> res = cssFeign.updateBySubServiceId(subServiceId, domainServices);
    		return res.getBody();
    	 
	    		}catch(FeignException e) {
    	            response.setStatus(500);
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
	    			return response;
    	        }
                    
	}
    @Override
	public Response getAllSubServices(){
		Response response = new Response();
    	try {
    		ResponseEntity<Response> res = cssFeign.getAllSubServices();
    		return res.getBody();
    	 
	    		}catch(FeignException e) {
    	            response.setStatus(500);
	    			response.setMessage(ExtractFeignMessage.clearMessage(e));
	    			response.setSuccess(false);
	    			return response;
    	        }
                     } 
	
	
	
	// CUSTOMER MANAGEMENT
    
    @Override
	public Response saveCustomerBasicDetails(CustomerDTO customerDTO ) {
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<Response> res = customerFeign.saveCustomerBasicDetails(customerDTO);
	    		  if(res != null) {
	    			  Response rs = res.getBody();
	    			  return rs;
	    		  }}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;	
}
    @Override	
	public ResponseEntity<?> getCustomerByUsernameMobileEmail(String input) {
	    	try {
	    		ResponseEntity<?> res = customerFeign.getCustomerByUsernameMobileEmail(input);
	    		  if(res != null) {
	    		 return res;}
	    		  else {
	    			  return ResponseEntity.status(404).body("Customer Details Not Found");
	    		  }
	    		  }catch(FeignException e) {
	    			  return ResponseEntity.status(e.status()).body(ExtractFeignMessage.clearMessage(e));
	    	        }
	                }
    @Override
	public Response getCustomerBasicDetails(String mobileNumber ) {
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<Response> res = customerFeign.getCustomerBasicDetails(mobileNumber);
	    		  if(res != null) {
	    			  Response rs = res.getBody();
	    			  return rs;
	    		  }}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;	
}

    @Override
	public Response getAllCustomers(){
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<Response> res = customerFeign.getAllCustomers();
	    		  if(res != null) {
	    			  Response rs = res.getBody();
	    			  return rs;
	    		  }}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;	
}
	
    @Override
	public Response updateCustomerBasicDetails(CustomerDTO customerDTO,String mobileNumber ){
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<Response> res = customerFeign.updateCustomerBasicDetails(customerDTO, mobileNumber);
	    		  if(res != null) {
	    			  Response rs = res.getBody();
	    			  return rs;
	    		  }}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;	
}
	
    @Override
	public Response deleteCustomerBasicDetails(String mobileNumber){
		 Response response = new  Response();
	    	try {
	    		ResponseEntity<Response> res = customerFeign.deleteCustomerBasicDetails(mobileNumber);
	    		  if(res != null) {
	    			  Response rs = res.getBody();
	    			  return rs;
	    		  }}catch(FeignException e) {
	    	            response.setStatus(e.status());
		    			response.setMessage(ExtractFeignMessage.clearMessage(e));
		    			response.setSuccess(false);
	    	        }
	                    return response;	
}
		
}

