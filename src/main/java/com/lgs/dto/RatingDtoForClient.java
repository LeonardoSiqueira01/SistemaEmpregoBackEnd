package com.lgs.dto;

public class RatingDtoForClient {
		private String ProfessionalName;
	    private String commentaryForClient;
	    private Double ratedClient;

	    // Construtores, Getters e Setters

	    public RatingDtoForClient(String ProfessionalName, String commentaryForClient, Double ratedClient) {
	       this.ProfessionalName =ProfessionalName;
	    	this.commentaryForClient = commentaryForClient;
	        this.ratedClient = ratedClient;
	    }



		public Double getRatedClient() {
			return ratedClient;
		}

		public void setRatedClient(Double ratedClient) {
			this.ratedClient = ratedClient;
		}


		public String getProfessionalName() {
			return ProfessionalName;
		}


		public void setProfessionalName(String professionalName) {
			ProfessionalName = professionalName;
		}


		public String getCommentaryForClient() {
			return commentaryForClient;
		}


		public void setCommentaryForClient(String commentaryForClient) {
			this.commentaryForClient = commentaryForClient;
		}

	    
	}
