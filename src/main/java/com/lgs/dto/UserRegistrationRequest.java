package com.lgs.dto;

import com.lgs.entities.User;

public class UserRegistrationRequest {
	 private User user = new User();
	    private String userType;
	    private String specialties;
	    private String location;
	    
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public String getUserType() {
			return userType;
		}
		public void setUserType(String userType) {
			this.userType = userType;
		}
		public String getSpecialties() {
			return specialties;
		}
		public void setSpecialties(String specialties) {
			this.specialties = specialties;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		
		
	    
}
