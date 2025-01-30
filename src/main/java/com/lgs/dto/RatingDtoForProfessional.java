package com.lgs.dto;

public class RatingDtoForProfessional {
		private String ClientName;
	    private String comment;
	    private Double ratedProfessional;


	  
		public RatingDtoForProfessional(String clientName, String comment, Double ratedProfessional) {
			ClientName = clientName;
			this.comment = comment;
			this.ratedProfessional = ratedProfessional;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}


		public String getClientName() {
			return ClientName;
		}

		public void setClientName(String clientName) {
			ClientName = clientName;
		}

		public Double getRatedProfessional() {
			return ratedProfessional;
		}

		public void setRatedProfessional(Double ratedProfessional) {
			this.ratedProfessional = ratedProfessional;
		}

	    
	}
