package fr.pokegoboost.responses;

import fr.pokegoboost.wrapper.ProfileWrapper;
import fr.pokegoboost.wrapper.Result;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProfileResponse {
	
	private ProfileWrapper	profile;
	
	private Result			result;
}
